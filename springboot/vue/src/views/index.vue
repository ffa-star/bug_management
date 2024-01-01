<template>
  <div class="home">
    <el-container>
      <el-header style="height:70px;background: #001F3F; ">
        <el-button type="primary" round size="danger" style="margin-left:-10%;margin-top:10px;font-size:20px;font:bold;width:130px" @click="writeArticle">整理bug</el-button>
        <el-button type="primary" round  size = "meduim" style="margin-left:35px;margin-right:15%;margin-top:10px;font-size:20px;font:bold;width:130px" @click="$router.push('/resource')">学习资源</el-button>
        <el-input placeholder="请输入内容" v-model="name" clearable suffix-icon="el-icon-search" style="width:250px;color:white">
        </el-input>

        <el-button type="primary" style="margin-left:10px" @click="getarticle">搜索</el-button>


        <div class="right-content">
          <el-dropdown style="width: 100px; cursor: pointer" trigger="click">
            <div style="display: inline-block">
              <span style="color:white">{{ user.username }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center">
              <el-dropdown-item style="font-size: 14px; padding: 5px 0">个人信息</el-dropdown-item><el-dropdown-item
                style="font-size: 14px; padding: 5px 0">
                <router-link to="/login" style="text-decoration: none">退出</router-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>


      </el-header>

      <el-container>
        <el-main width="60%" style="background: linear-gradient(to bottom, #F5F5F5 10%, #A9A9A9);">
          <div v-for="(item, index) in articles" style="margin-bottom:2px;width:100%;" :key="index">
            <el-card class="box-card" style="height:120px;cursor:pointer">
              <div slot="header" class="clearfix" style="height:20px;width:100%"
                @click="$router.push('/articleDetail?id=' + item.id)">
                <span style="text-align:center;font-size:20px;color:#3399cc; width:50%">{{ item.title }}</span>
                <span style="margin-left:10px;padding-left:5px">
                  <el-tag style="margin-right:5px" v-for="(tagitem, i) in dynamicTags[index]" :key="i">{{ tagitem }} </el-tag>
                  


                </span>
              </div>
              <div style="height:100px;font-size:14px">
                <i class="el-icon-user-solid"></i> <span>{{ item.username }}</span>
                <i class="el-icon-item" style="margin-left:10px"></i><span>{{ item.updateTime.replace("T", " ") }}</span>
              </div>
            </el-card>
          </div>

          <div style="height:30px;margin-top:15px">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
              :page-sizes="[5, 10, 15, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>

        </el-main>
        <div style="width:40%;background: linear-gradient(to bottom, #F5F5F5 10%, #A9A9A9);">
          <div style="margin-top: 20px;">
            <h1 style="text-align:center;margin-bottom:20px;margin-right:180px;margin-top:20px">讯飞星火认知大模型</h1>
            <el-input :rows="5" placeholder="请输入内容" v-model="userInput" clearable suffix-icon="el-icon-search"
              style="width:70%; margin-top:10px;margin-left:15px;" type="textarea">
            </el-input>

            <el-button @click="start" type="primary" style="margin-left:10px;width:20%" :loading="loading">{{ loading ?
              '在拼命思考中' : '提问' }}</el-button>

          </div>
          <div>
            <div v-for="(chatConten, index) in finalChat" :key="index">
              
              <mavon-editor class="md" :value="chatConten.ai" :subfield="false" :defaultOpen="'preview'"
                :toolbarsFlag="false" :editable="false" :scrollStyle="true" :ishljs="true" style="margin-top:5px"/>
            </div>
          </div>
        </div>
      </el-container>
    </el-container>
  </div>
</template>


<style>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  text-align: center;
  line-height: 60px;
  height: 30vh;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  text-align: center;
  line-height: 200px;
  height: 92vh;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
  text-align: center;

  height: 92vh;
}

body>.el-container {
  margin-bottom: 40px;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}


.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  width: 100%;

}

.right-content {
  position: fixed;
  right: 22%;
  top: -18px;
  width: 50px;
  /* 根据需要调整宽度 */
  /* height: 100%; 根据需要调整高度 */

  padding: 20px;
  /* 根据需要调整内边距 */
}
</style>

<script>
import CryptoJS from 'crypto-js';
export default {
  data() {
    return {
      name: "",
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      articles: [],
      pageNum: 1,
      pageSize: 5,

      total: 0,
      dynamicTags: [],

      appId: 'bb85d099',
      status: 0,
      ttsWS: null,
      totalRes: '',
      userInput: '',
      aiContentRequest: '',
      finalChat: [],
      loading: false

    }
  },
  methods: {
    
    getarticle() {
      
      this.request.get("/articles/page",
        {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.name
          }
        }).then(res => {
          console.log(res),
            this.articles = res.records,
            this.total = res.total,
            
            res.records.map((item) => {
              this.dynamicTags.push(item.tag?item.tag.split(","):null)
            });
            console.log(this.dynamicTags)
            
        })
        
       
    },
    writeArticle() {
      this.$router.push("/write")
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.getarticle();

    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.getarticle();
    },



    getWebsocketUrl() {
      return new Promise((resolve, reject) => {
        const apiKey = '5b25d5bb04d7275e123621a6efe1c51b';
        const apiSecret = 'NDRjY2JhMGFiNzAyZDFjZTRhYTFiYjQ5';
        const url = 'wss://spark-api.xf-yun.com/v1.1/chat'; //这里使用的是星火大模型2.x版本
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
            max_tokens: 1024,
          },
        },

        payload: {
          message: {
            text: [
              { "role": "user", "content": that.userInput }
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
        this.finalChat.push(contentSomething)
        this.userInput = ''
        this.loading = false
      }
      if (jsonData.header.code !== 0) {
        alert(`提问失败: ${jsonData.header.code}:${jsonData.header.message}`);
        console.error(`${jsonData.header.code}:${jsonData.header.message}`);
        return;
      }
      if (jsonData.header.code === 0 && jsonData.header.status === 2) {
        this.ttsWS.close();
        this.setStatus('init');
      }
    },
  },


  created() {
    this.getarticle();
  }

}
</script>