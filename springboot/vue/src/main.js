import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import request from './utils/request';
//main.js全局注册
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
import WebSocketService from './utils/llm'; // 请根据实际路径调整

Vue.use(WebSocketService);
//use
Vue.use(mavonEditor)
Vue.use(ElementUI,{size:'mini'});
Vue.prototype.request=request;
Vue.config.productionTip = false


new Vue({
  router,
  render: h => h(App)
}).$mount('#app')