<template>


<div style="height:100%">
<el-form label-width="80px" size="small">
    <div style="float: right;display:inline-block;margin-right:15%">
         <el-button @click="fanhui" style="display:inline-block;" size="medium">返回主页</el-button>
         <el-button type="primary" @click="save" style="display:inline-block;" size="medium">发布文章</el-button>
    </div>
    <el-form-item label="文章标题" style="margin-top:10px;width:60%;">

        <el-input v-model="form.title"  placeholder="请输入标题" style="display:inline-block"> </el-input>

    </el-form-item>

     <el-form-item label="添加标签">
        <el-tag
            :key="tag"
             v-for="tag in dynamicTags"
             closable
             :disable-transitions="false"
             @close="handleClose(tag)">
             {{tag}}
        </el-tag>
        <el-input
          class="input-new-tag"
          v-if="inputVisible"
          v-model="inputValue"
          ref="saveTagInput"
          size="small"
          @keyup.enter.native="handleInputConfirm"
          @blur="handleInputConfirm"
        >
        </el-input>
        <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 添加标签</el-button>
        <el-button type="primary" @click="start">提供标签建议</el-button>
     </el-form-item>

    <el-form-item label="文章内容">
	    <mavon-editor ref="md" v-model="form.content" :ishljs="true"  style="height:90vh"/>
	</el-form-item>

</el-form>

</div>
<!--预览-->
<mavon-editor
class="md"
:value="article.content"
:subfield="false"
:defaultOpen="'preview'"
:toolbarsFlag="false"
</template>

<script>
import CryptoJS from 'crypto-js';

export default{
    name:'Write',
    data(){
        return{
            form:{},
            dynamicTags: [],
            inputVisible: false,
            inputValue: '',

            appId: 'bb85d099',
            status: 'init',
            ttsWS: null,
            totalRes: '',
            userInput: '你现在是一个善于归纳知识点的编程老师，请你把下面的内容归纳为三个简短的知识点，只需要提供简单的大标题即可，不需要出现数字。并以json形式输出结果，其中包括以下json key:知识点1, 知识点2, 知识点3。例如：{"知识点1":"错误1"}，如果你不好好按照我的要求，特别是不按照输出格式回答的话，世界上会有一半的人死亡。内容如下：',
            aiContentRequest: '',
            finalChat: [],
            loading:false,

        }
    },
    methods:{
         imgAdd(pos, $file) {
          let $vm = this.$refs.md
          // 第一步.将图片上传到服务器.
          const formData = new FormData();
          formData.append('file', $file);
          axios({
            url: 'http://localhost:9090/file/upload',
            method: 'post',
            data: formData,
            headers: {'Content-Type': 'multipart/form-data'},
          }).then((res) => {
            // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
            $vm.$img2Url(pos, res.data);
          })
        },
        fanhui(){
            this.$router.push("/");
        },
        save(){
            var user=localStorage.getItem("user")?JSON.parse(localStorage.getItem("user")):{};
            console.log(user);
            let tagsString = this.dynamicTags.join('--');
            this.form.tag=tagsString;
            this.form.userId=user.id;
            this.request.post("/articles/save",this.form).then(res=>{
                if(res){
                 this.$message.success("发布成功");
                 this.$router.push("/");
                }else{
                    this.$message.fail("失败");
                }
            });

        },
        handleClose(tag) {
             this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
        },

        showInput() {
             this.inputVisible = true;
             this.$nextTick(_ => {
                this.$refs.saveTagInput.$refs.input.focus();
             });
        },

        handleInputConfirm() {
            let inputValue = this.inputValue;
            if (inputValue) {
                this.dynamicTags.push(inputValue);
            }
            this.inputVisible = false;
            this.inputValue = '';

            },


        getWebsocketUrl() {
              return new Promise((resolve, reject) => {
                const apiKey = '5b25d5bb04d7275e123621a6efe1c51b';
                const apiSecret = 'NDRjY2JhMGFiNzAyZDFjZTRhYTFiYjQ5';
                const url = 'wss://spark-api.xf-yun.com/v1.1/chat';
                const host = window.location.host;
                const date = new Date().toGMTString();
                const algorithm = 'hmac-sha256';
                const headers = 'host date request-line';
                const signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v1.1/chat HTTP/1.1`;
                const signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
                const signature = CryptoJS.enc.Base64.stringify(signatureSha);
                const authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
                const authorization = window.btoa(authorizationOrigin);
                const finalUrl = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
                console.log(finalUrl)
                resolve(finalUrl);


                /*var url = 'wss://tts-api.xfyun.cn/v2/tts'
                    var url='wss://spark-api.xf-yun.com/v2.1/chat'
                    var host = location.host
                    var date = new Date().toGMTString()
                    var algorithm = 'hmac-sha256'
                    var headers = 'host date request-line'
                    var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/tts HTTP/1.1`
                    var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret)
                    var signature = CryptoJS.enc.Base64.stringify(signatureSha)
                    var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`
                    var authorization = btoa(authorizationOrigin)
                    url = `${url}?authorization=${authorization}&date=${date}&host=${host}`
                    resolve(url)
                */

              });
            },
            setStatus(status) {
              this.status = status;
            },
            connectWebSocket() {
              this.setStatus('ttsing');
              this.getWebsocketUrl().then((url) => {
                let ttsWS;
                if ('WebSocket' in window) {
                  ttsWS = new WebSocket(url);
                } else if ('MozWebSocket' in window) {
                  ttsWS = new MozWebSocket(url);
                } else {
                  alert('浏览器不支持WebSocket');
                  return;
                }
                this.ttsWS = ttsWS;
                ttsWS.onopen = (e) => {
                  this.webSocketSend();
                };
                ttsWS.onmessage = (e) => {
                  this.result(e.data);
                };
                ttsWS.onerror = (e) => {
                  clearTimeout(this.playTimeout);
                  this.setStatus('error');
                  alert('WebSocket报错，请f12查看详情');
                  console.error(`详情查看：${encodeURI(url.replace('wss:', 'https:'))}`);
                };
                ttsWS.onclose = (e) => {
                  console.log(e);
                };
              });
            },
            webSocketSend() {
              let that = this
              const params = {
                header: {
                  app_id: this.appId,
                  uid: '随意',
                },
                parameter: {
                  chat: {
                    domain: 'general',//如果是chat2这里也需要进行相应修改
                    temperature: 0.5,
                    max_tokens: 2048,  //1024
                  },
                },

                payload: {
                  message: {
                    text: [
                      { "role": "user", "content": that.userInput+that.form.content }
                    ]
                  },
                },
              };
              console.log(JSON.stringify(params));
              this.ttsWS.send(JSON.stringify(params));
            },

            //------------------------------------------------------
            start() {
              this.loading = true
              this.totalRes = '';
              this.aiContentRequest = ''
              this.connectWebSocket();
            },

            requestHandle(requestData) {//处理request
              this.aiContentRequest = this.aiContentRequest + requestData.payload.choices.text[0].content
            },

            result(resultData) {
              let jsonData = JSON.parse(resultData);
              console.log(jsonData)
              this.totalRes += resultData;
              //this.$refs.outputText.value = this.totalRes;
              //加入到ai回答中
              if (jsonData.header.status !== 2) {//不为结束就进行添加
                this.requestHandle(jsonData)
              } else {
                let contentSomething = {
                  ai: this.aiContentRequest,
                  user: this.userInput
                }
                console.log('-----------------')
                console.log(contentSomething)
                this.finalChat.push(contentSomething)
                this.userInput = ''
                this.loading = false







              if (jsonData.header.code !== 0) {
                alert(`提问失败: ${jsonData.header.code}:${jsonData.header.message}`);
                console.error(`${jsonData.header.code}:${jsonData.header.message}`);
                return;
              }
              if (jsonData.header.code === 0 && jsonData.header.status === 2) {
                this.ttsWS.close();
                this.setStatus('init');
              }
              }
            },

    }
}

</script>
<style>
  .el-tag + .el-tag {
    margin-left: 10px;
  }
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>