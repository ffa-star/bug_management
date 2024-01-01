<template>
  <div class="home" style="height:100%">
   <el-container style="height: 100%; width:100%;">
     <el-aside :width="sidewith+'px'" style="background-color: rgb(124, 200, 255);height:100%;" :collapse=true>
       <Aside/>

     </el-aside>

     <el-container>
       <el-header style="text-align: right; font-size: 12px">

         <el-dropdown>
           <i class="el-icon-s-fold" style="margin-right: 15px" @click="collapse"></i>
           <el-dropdown-menu slot="dropdown">
             <el-dropdown-item>查看</el-dropdown-item>
             <el-dropdown-item>新增</el-dropdown-item>
             <el-dropdown-item>删除</el-dropdown-item>
           </el-dropdown-menu>
         </el-dropdown>
         <span>王小虎</span>
       </el-header>
       <div class="ml-5" style="margin-top:10px;margin-left:20px">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item><a href="/">用户管理</a></el-breadcrumb-item>
            </el-breadcrumb>
            </div>
       <el-main style="height:80%">
       <div class="ml-5">
           <el-input style="width:200px" suffix-icon="el-icon-search" placeholder="请输入名称" v-model="username"></el-input>
           <el-input style="width:200px" suffix-icon="el-icon-search" placeholder="请输入邮箱" class="ml-5" v-model="email"></el-input>
           <el-input style="width:200px" suffix-icon="el-icon-search" placeholder="请输入地址" class="ml-5" v-model="address"></el-input>
           <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
       </div>
       <div class="ml-5" style="margin-top:10px">
            <el-button class="ml-5" type="primary" @click="handleAdd">新增<i class="el-icon-circle-plus-outline" style="margin-left:5px"></i></el-button>
            <el-popconfirm
                              class="ml-5"
                              confirm-button-text='确定'
                              cancel-button-text='再想想'
                              icon="el-icon-info"
                              icon-color="red"
                              title="确定删除吗？"
                              @confirm="deleteBatch"
                            >

            <el-button class="ml-5" type="danger"slot="reference"  >批量删除<i class="el-icon-remove-outline" style="margin-left:5px"></i></el-button>
                            </el-popconfirm>

            <el-button class="ml-5" type="primary" >导入<i class="el-icon-bottom" style="margin-left:5px"></i></el-button>
            <el-button class="ml-5" type="primary" >导出<i class="el-icon-top" style="margin-left:5px"></i></el-button>
       </div>
         <el-table :data="tableData" style="height:100%" @selection-change="handleSelectionChange">
         <el-table-column
               type="selection"
               width="55">
             </el-table-column>
           <el-table-column prop="username" label="用户名" width="140">
           </el-table-column>
           <el-table-column prop="nickname" label="昵称" width="120">
           </el-table-column>
           <el-table-column prop="email" label="邮箱" width="160">
           </el-table-column>
           <el-table-column prop="phone" label="电话" width="120">
            </el-table-column>
           <el-table-column prop="address" label="地址">
           </el-table-column>
           <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button type="success" @click="handleEdit(scope.row)">编辑</el-button>
                <el-popconfirm
                  class="ml-5"
                  confirm-button-text='确定'
                  cancel-button-text='再想想'
                  icon="el-icon-info"
                  icon-color="red"
                  title="确定删除吗？"
                  @confirm="handleDelete(scope.row)"
                >
                <el-button type="danger" slot="reference">删除</el-button>

                </el-popconfirm>

            </template>
           </el-table-column>

         </el-table>

        <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
          <el-form label-width="120px" size="small">
            <el-form-item label="用户名">
              <el-input v-model="form.username" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="昵称">
               <el-input v-model="form.nickname" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
                <el-input v-model="form.email" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="电话">
                <el-input v-model="form.phone" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="住址">
                <el-input v-model="form.address" autocomplete="off"></el-input>
            </el-form-item>


            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </div>
        </el-dialog>
       </el-main>

       <div style="height:80px">
                   <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                      :current-page="pageNum"
                      :page-sizes="[5,10, 15, 20]"
                      :page-size="pageSize"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="total">
                    </el-pagination>
                </div>
     </el-container>
   </el-container>
  </div>
</template>

<script>
    //import request from "@/utils/request";
    import Aside from "@/components/Aside";
     export default {
       data() {
         return {
           tableData: [],
           isCollapse:false,
           sidewith:200,
           total:0,
           pageNum:1,
           pageSize:10,
           username:"",
           email:"",
           address:"",
           dialogFormVisible:false,
           form:{},
           multipleSelection: []
         }
       },
       components:{Aside},
       methods:{
            collapse(){
                if(this.isCollapse){
                    this.sidewith=200;
                    this.isCollapse = !this.isCollapse;
                }else{
                   this.sidewith = 0;
                   this.isCollapse = !this.isCollapse;
                 }
            },
            load(){
                this.request.get("/user/page",{
                    params:{
                        pageNum:this.pageNum,
                        pageSize:this.pageSize,
                        username:this.username,
                        email:this.email,
                        address:this.address
                    }
                }).
                    then(res=>{
                        this.tableData=res.records;
                        this.total=res.total;
                    })
            },
            save(){
            this.request.post("/user/save",this.form).
                                then(res=>{
                                    if(res){
                                        this.$message.success("成功");
                                        this.dialogFormVisible=false;
                                        this.load();
                                    }else{
                                    this.$message.success("失败");
                                    }

                                })
            },
            handleEdit(row){
                this.form=row;
                console.log(row);
                this.dialogFormVisible=true;
            },
            handleDelete(row){
                this.request.delete("/user/"+row.id).then(res=>{
                    if(res){
                       this.$message.success("删除成功");
                       this.load();
                    }else{
                       this.$message.success("失败");
                    }
                })
            },
            handleSizeChange(pageSize){
                this.pageSize=pageSize;
               this.load();
            },
            deleteBatch(){
                let ids = this.multipleSelection.map(v => v.id);
                this.request.post("/user/del/batch",ids).then(res=>{
                    if(res){
                        this.$message.success("批量删除成功");
                        this.load();
                    }else{
                        this.$message.success("批量删除失败");
                    }
                })
            },
            handleSelectionChange(val){
                this.multipleSelection=val;
                console.log(val);

            },
            handleCurrentChange(pageNum){
                this.pageNum=pageNum;
                this.load();

            },
            handleAdd(){
                this.dialogFormVisible=true;
                this.form={};
            }
       },

       created(){
            //请求分页查询数据
            this.load();
            }

     };
   </script>
<style>
     .el-header {
       background-color: #B3C0D1;
       color: #333;
       line-height: 60px;
     }

     .el-aside {
       color: #333;
     }
   </style>