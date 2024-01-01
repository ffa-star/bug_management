<template>
  <div class="home" style="background-image: url('../assets/OIP.jpg');">
    <el-container>

        <el-header style="height:70px;background: #001F3F; ">

         <span class="custom-text" style="margin-right:500px">好好学习</span>
     <el-button @click="$router.push('/')" style="margin-right:-500px;">返回主页</el-button>

        </el-header>

        <el-container>
        <el-main width="70%" class="bgp">
        <div ref="chart" class="dcontainer" style="width: 800px; height: 375px;margin-left:20%"></div>
         <div style="margin-left:0px;width:100%;">
             <el-select
size="medium"
             style="margin-top:15px;margin-left:-10px;width:80%;text-align:left;"
                v-model="value"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="请选择需要学习的标签">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>

          <el-button size="medium" type="primary" @click="search" style="width:10%;margin-left:10px;":loading="loading">{{ loading ? '在拼命思考中' : '生成学习内容' }}</el-button>
        </div>

        <div style="margin-top:20px;width:90%;margin-left:60px">
        <mavon-editor
                 class="md"
                 :value="learningresources"
                 :subfield="false"
                 :defaultOpen="'preview'"
                 :toolbarsFlag="false"
                 :editable="false"
                 :scrollStyle="true"
                 :ishljs="true"
             />
        </div>


        </el-main>

      </el-container>
    </el-container>
  </div>
</template>


<style>




.custom-text{
    font-size: 30px;
    font-weight: bold;
    color: white;
    letter-spacing: 8px;
    line-height:9vh;
}
.el-header {
  background-color: #B3C0D1;
  color: #333;
  text-align: center;
  line-height: 60px;
  height: 30vh;
}

.bgp{
/*background-image: url('../assets/study.png');*/
background-size: cover; /* 可以根据需要调整图片的尺寸适应 */
  background-position: center; /* 可以根据需要调整图片的位置 */
  background-repeat: no-repeat; /* 确保背景图不重复 */
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




</style>

<script>
import CryptoJS from 'crypto-js';
import * as echarts from 'echarts';
export default {
  data() {
    return {
      name: "",
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      tags: [],
      pageNum: 1,
      pageSize: 5,
      learningresources:"",


      total: 0,
      dynamicTags: [],

      appId: 'bb85d099',
      status: 0,
      ttsWS: null,
      totalRes: '',

      userInput: '你现在是一位编程教师，我现在有如下的知识点，这些知识点是我在编程的过程中出错的知识点或者不会的知识点，请根据这些知识点用markdown格式给我生成针对性的学习资料。首先你需要为每个知识点生成详细的文字性的可供参考的学习内容和学习资料，可以填写一些相关的链接，需要添加一些案例，最后需要根据知识点给出习题和答案。然后你需要判断这些知识点之间是否有联系，如果有联系，再给出联系后的学习内容，内容如下:',
      aiContentRequest: '',
      finalChat: [],
      loading: false,

      options: [{
                value: 'HTML',
                label: 'HTML'
              }, {
                value: 'CSS',
                label: 'CSS'
              }, {
                value: 'JavaScript',
                label: 'JavaScript'
              }],
      value: []

    }
  },
  methods: {

    search(){
        this.userInput=this.userInput+this.value;
        this.start();
    },
    setStatus(status) {
            this.status = status;
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
            "header": {"app_id": this.appId},
            "parameter": {
                "chat": {
      // 指定访问的领域,general指向V1.5版本,generalv2指向V2版本,generalv3指向V3版本 。
      // 注意：不同的取值对应的url也不一样！
                "domain": "generalv3",
      // 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
                "temperature": 0.5,
      // 模型回答的tokens的最大长度
                "max_tokens": 8000
                }
            },
        payload: {
                    message: {
                      text: [
                        { "role": "user", "content": this.userInput+this.value }
                      ]
                    },
        },
        }
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
              this.learningresources=contentSomething.ai;
               this.userInput = ""




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
  },


  created() {
      this.request.get("/resources/" + this.user.id).then(res => {
        this.tags = Object.keys(res);
        console.log(this.tags);
        this.options = this.tags.map((key) => ({
              value: key,
              label: key,
        }));
        const chartContainer = this.$refs.chart;
              // 初始化echarts实例
        const myChart = echarts.init(chartContainer);
              // 将对象的键转换为数组
        const slicedKeysArray = this.tags.slice(0, 10);

              // 转换成所需的格式
              const transformedOptions = slicedKeysArray.map((key) => ({
                value: res[key],
                label: key,
              }));
              // 配置图表
        const options = {
                title: {
                  text: 'BUG标签统计图',
                },
                xAxis: {
                  type: 'category',
                  data: slicedKeysArray,
                  axisLabel: {
                              // 设置x轴标签的显示样式
                              interval: 0, // 强制显示所有标签
                              rotate:20 , // 旋转45度，可以根据需要调整旋转角度
                            },
                },
                yAxis: {
                  type: 'value',
                },
                tooltip: {
                          trigger: 'axis',
                          axisPointer: {
                            type: 'shadow', // 鼠标悬停时的阴影效果
                          },
                        },
                series: [
                  {
                    name: '标签数量',
                    type: 'bar',
                    data: transformedOptions,
                    itemStyle: {
                                  // 设置柱状图颜色，可以根据需要调整
                                  color: (params) => {
                                    const colorList = ['#3398DB', '#66CCFF', '#99CCFF', '#FF9966', '#FFCCCC', '#FF6666', '#66FF66'];
                                    return colorList[params.dataIndex % colorList.length];
                                  },
                                },
                  },
                ],
        };

              // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(options);
      })

  }

}
</script>