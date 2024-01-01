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
        size="medium"
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
        <el-button type="primary" @click="start" :loading="loading">{{ loading ?
                                                               '在拼命思考中' : '提供标签建议' }}</el-button>
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
            userInput: '你现在是一个善于归纳知识点的编程老师，请你把下面的内容归纳为三个简短的知识点，只需要提供简单的大标题即可，不需要出现数字。并以json形式输出结果，其中包括以下json key:知识点1, 知识点2, 知识点3。，如果你不好好按照我的要求，特别是不按照输出格式回答的话，世界上会有一半的人死亡。内容如下：',
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
            let tagsString = this.dynamicTags.join(',');
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
 setStatus(status) {
              this.status = status;
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
                var apiKey = '5b25d5bb04d7275e123621a6efe1c51b'
                var apiSecret = 'NDRjY2JhMGFiNzAyZDFjZTRhYTFiYjQ5'
                var url = 'ws://spark-api.xf-yun.com/v3.1/chat'
                var host = location.host
                var date = new Date().toGMTString()
                var algorithm = 'hmac-sha256'
                var headers = 'host date request-line'
                var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v3.1/chat HTTP/1.1`
                var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret)
                var signature = CryptoJS.enc.Base64.stringify(signatureSha)
                var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`
                var authorization = btoa(authorizationOrigin)
                url = `${url}?authorization=${authorization}&date=${date}&host=${host}`
                resolve(url)
              })
            },

              // 连接websocket
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


              // websocket发送数据
              webSocketSend() {
                var params = {
                  "header": {
                    "app_id": this.appId,
                  },
                  "parameter": {
                    "chat": {
                      // 指定访问的领域,general指向V1.5版本,generalv2指向V2版本,generalv3指向V3版本 。
                      // 注意：不同的取值对应的url也不一样！
                      "domain": "generalv3",
                      // 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
                      "temperature": 0.5,
                      // 模型回答的tokens的最大长度
                      "max_tokens": 1024
                    }
                  },
                  payload: {
                                    message: {
                                      text: [
                                        { "role": "user", "content": this.userInput+this.form.content }
                                      ]
                                    },
                                  },
                }
                console.log(params,'请求的参数')
                this.ttsWS.send(JSON.stringify(params))
              },

              start() {
                            this.loading = true
                            this.totalRes = '';
                            this.aiContentRequest = ''
                            this.connectWebSocket();
                          },

              // websocket接收数据的处理
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
                              console.log(contentSomething.ai)

                             contentSomething.ai = contentSomething.ai.trim().replace(/[\r\n]+/g, '').replace(/,$/, '');
                              //contentSomething.ai=contentSomething.ai.slice(0, -1);

                              let final = contentSomething.ai+'}'
                              console.log(final)
                              let jsonData1 = JSON.parse(final);
                              console.log(jsonData1);

                              this.dynamicTags = this.dynamicTags.concat(Object.values(jsonData1));
                              console.log(this.dynamicTags)

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