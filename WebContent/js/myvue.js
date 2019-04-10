var tiku=new Vue({
  el: '#vueEntry',
  data: { 
    navAppend: '隐藏',  

    quchong: '重复题处理',
    tikuguanl: '库管理',
    peizhi: '配置文件内容',
    createPapers: '生成试卷',
    chouti: '试卷抓题',
    classify: '试题分类',

    docProperties:"",
	databaseProperties:{},
    showProperties:{},
    VcreatePapers: false,
    Vchouti: true,
    Vquchong: false,
    Vtikuguanl: false,
    Vpeizhi: false,            
    Vclassify: false,
	
	quchongresult:"",

    choutifilename:"",
    showchoutifilename:"",
    choutiresult:"",

    createpaperresult:"",
	
	progressmask:false,

    navshow: true,
	
	p_merge_per:"100",

	p_tempdatabase:'',p_database:'',
	p_school:'',p_semester:'',p_subject:'',p_class:'',p_time:'',
	p_template:'',p_file_prefix:'',p_diffcopies:'',
	p_单选_total:'',p_多选_total:'',p_不定项_total:'',p_填空_total:'',p_判断_total:'',p_简答_total:'',
	p_单选_order:'',p_多选_order:'',p_不定项_order:'',p_填空_order:'',p_判断_order:'',p_简答_order:'',
	p_单选_random:'',p_多选_random:'',p_不定项_random:'',p_填空_random:'',p_判断_random:'',p_简答_random:'',
	p_avoidused:'',
    
    list: [{code:"templateA3.docx",name:'A3'},{code:"templateA4.docx",name:'A4'}],
	avoidusedcontrol:[{code:"true",name:'是'},{code:"false",name:'否'}]

  } ,
  methods: { 
		shownav: function(){
		  this.navshow=!this.navshow;
		  if (!this.navshow){
			  this.navAppend="显示";
		  }else{ this.navAppend="隐藏";}
		},
		 setVisabled:function (type){
		  $(".leftNav").css("background-color","");
		  $("#"+type).css("background-color","lightblue");
		  this.Vquchong=false;
		  this.Vtikuguanl= false;
		  this.Vpeizhi=false;
		  this.VcreatePapers=false;
		  this.Vchouti=false;
		  this.Vclassify=false;
		switch(type){
			case "Vquchong":
			  this.Vquchong=true;
				break;
			case "Vtikuguanl":
			  this.Vtikuguanl=true;
				break;
			case "Vpeizhi":
			  this.Vpeizhi=true;
				break;
			case "VcreatePapers":
			  this.VcreatePapers=true;
				break;
			case "Vchouti":
			  this.Vchouti=true;
				break;
			case "Vclassify":
			  this.Vclassify=true;
				break;

		}
		}, 

		FInitProperties:function(){
		  var json={"properties":{}};
		  if (tiku.$data.docProperties==""){
			  $.ajax({
					type: "get",
					dataType: "json",
					contentType:"application/json;charset=utf-8",
					//url:  "api",
					url:  "api?command=docProperties",
					data: JSON.stringify(json),
					async:false,
					success: function (data) {
					  
						tiku.$data.docProperties=data;
						tiku.$data.showProperties.卷首={};
						tiku.$data.showProperties.试卷规格={};
						tiku.$data.showProperties.题目顺序={};
						tiku.$data.showProperties.题目总分={};
						tiku.$data.showProperties.题目数量={};
					  
					},
					error:function(data){
					  console.log(data); 
					}
				});
			}
		},
		FSetShow:function(){
		  this.databaseProperties.缓存文件夹=this.docProperties.tempdatabase
		  this.databaseProperties.试题文件夹=this.docProperties.database
		  this.showProperties.卷首.学校=this.docProperties.school
		  this.showProperties.卷首.学期=this.docProperties.semester
		  this.showProperties.卷首.学科=this.docProperties.subject
		  this.showProperties.卷首.班级=this.docProperties.class
		  this.showProperties.卷首.考试时间=this.docProperties.time

		  this.showProperties.试卷规格.母版=this.docProperties.template
		  this.p_template=this.docProperties.template
		  this.showProperties.试卷规格.文件名=this.docProperties.file_prefix
		  this.showProperties.试卷规格.分卷数=this.docProperties.diffcopies

		  this.showProperties.题目总分.单选=this.docProperties.单选_total
		  this.showProperties.题目总分.多选=this.docProperties.多选_total
		  this.showProperties.题目总分.不定项=this.docProperties.不定项_total
		  this.showProperties.题目总分.填空=this.docProperties.填空_total
		  this.showProperties.题目总分.判断=this.docProperties.判断_total
		  this.showProperties.题目总分.简答=this.docProperties.简答_total

		  this.showProperties.题目顺序.单选=this.docProperties.单选_order
		  this.showProperties.题目顺序.多选=this.docProperties.多选_order
		  this.showProperties.题目顺序.不定项=this.docProperties.不定项_order
		  this.showProperties.题目顺序.填空=this.docProperties.填空_order
		  this.showProperties.题目顺序.判断=this.docProperties.判断_order
		  this.showProperties.题目顺序.简答=this.docProperties.简答_order
		  

		  this.showProperties.题目数量.单选=this.docProperties.单选_random
		  this.showProperties.题目数量.多选=this.docProperties.多选_random
		  this.showProperties.题目数量.不定项=this.docProperties.不定项_random
		  this.showProperties.题目数量.填空=this.docProperties.填空_random
		  this.showProperties.题目数量.判断=this.docProperties.判断_random
		  this.showProperties.题目数量.简答=this.docProperties.简答_random
		  
		  this.showProperties.不选已用题目=this.docProperties.avoid_used_title
		  this.p_avoidused=this.docProperties.avoid_used_title
		  
		  this.p_merge_per=this.docProperties.单选_merge_per
		  
		},	
		FNav:function(type){
		  this.FInitProperties();
		  this.setVisabled(type);
		},
		FgetTemplateList:function(){
		  var json={"properties":{}};
		  $.ajax({
			type: "get",
			dataType: "json",
			contentType:"application/json;charset=utf-8",
			url:  "api?command=getTemplateName",
			data: JSON.stringify(json),
			async:false,
			success: function (data) {
				tiku.$data.list=data.template;
			},
			error:function(data){
			  console.log(data); 
			}
		});
		},
		changefilename: function () {
		  this.choutifilename = $("#input-b1").val(); 
		  this.choutifilename=this.choutifilename.split('fakepath') [1].replace('\\','') ;
		  this.showchoutifilename= "抓取试卷池指定试卷："+this.choutifilename;
		},
		quchongfunc:function(){
		  var json={"quchong":{}};		  
		  this.choutiresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.quchongresult=tiku.$data.choutifilename+":"+data.info;         
			  },
			  error:function(data){
				tiku.$data.quchongresult=tiku.$data.choutifilename+" ---- "+data.responseJSON.error; 
			  }
		  }); 
		},   
		choutifuncBatch:function(){
		  var json={"pickupBatch":{}};		  
		  this.choutiresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.choutiresult=tiku.$data.choutifilename+":"+data.info;         
			  },
			  error:function(data){
				tiku.$data.choutiresult=tiku.$data.choutifilename+" ---- "+data.responseJSON.error; 
			  }
		  }); 
		},   
		choutifunc:function(){
		  var json={"pickup":{"path":this.choutifilename}};
		  if (this.choutifilename==""){
			  this.createpaperresult="请先点击Browse，选择需要从中抓题的试卷。";
			  alert("请先点击Browse，选择需要从中抓题的试卷。");
			  this.choutiresult="未选择任何试卷，请先点击 '选择文件/Browse'，选择需要从中抓题的试卷。"
			  return;
		  }
		  this.choutiresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.choutiresult=tiku.$data.choutifilename+":"+data.info;         
			  },
			  error:function(data){
				tiku.$data.choutiresult=tiku.$data.choutifilename+" ---- "+data.responseJSON.error; 
			  }
		  }); 
		},
		createpaperfunc:function(){
		  $("#loadingModal").modal('show');
		  this.progressmask=true;
		  var json={"createpapers":{}};
		  this.createpaperresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.createpaperresult="试卷生成成功 ："+data.info;         
			  },
			  error:function(data){
				tiku.$data.createpaperresult="试卷生成失败 "+data.responseJSON.error;; 
				console.log(data.responseJSON.error);
			  }
		  });
			//this.progressmask=false;	  
			$("#loadingModal").modal('hide');
		},
		changeItem: function(name){
		  switch(name){
			case "p_school":
			  this.showProperties.卷首.学校=this.p_school
			  this.docProperties.school=this.p_school
			  break;
			case "p_semester":
			  this.showProperties.卷首.学期=this.p_semester
			  this.docProperties.semester=this.p_semester
			break;  
			case "p_subject":
			  this.showProperties.卷首.学科=this.p_subject
			  this.docProperties.subject=this.p_subject
			break; 
			case "p_class":
			  this.showProperties.卷首.班级=this.p_class
			  this.docProperties.class=this.p_class
			break; 
			case "p_time":
			  this.showProperties.卷首.考试时间=this.p_time
			  this.docProperties.time=this.p_time
			break;

			case "p_template":
			  this.showProperties.试卷规格.母版=this.p_template;
			  this.docProperties.template=this.p_template;
			break;
			case "p_file_prefix":
			  this.showProperties.试卷规格.文件名=this.p_file_prefix
			  this.docProperties.file_prefix=this.p_file_prefix
			break;
			case "p_diffcopies":
			  this.showProperties.试卷规格.分卷数=this.p_diffcopies
			  this.docProperties.diffcopies=this.p_diffcopies
			break; 

			case "p_单选_total":
			  this.showProperties.题目总分.单选=this.p_单选_total
			  this.docProperties.单选_total=this.p_单选_total
			break; 
			case "p_多选_total":
			  this.showProperties.题目总分.多选=this.p_多选_total
			  this.docProperties.多选_total=this.p_多选_total
			break; 
			case "p_不定项_total":
			  this.showProperties.题目总分.不定项=this.p_不定项_total
			  this.docProperties.不定项_total=this.p_不定项_total
			break; 
			case "p_填空_total":
			  this.showProperties.题目总分.填空=this.p_填空_total
			  this.docProperties.填空_total=this.p_填空_total
			break; 
			case "p_判断_total":
			  this.showProperties.题目总分.判断=this.p_判断_total
			  this.docProperties.判断_total=this.p_判断_total
			break; 
			case "p_简答_total":
			  this.showProperties.题目总分.简答=this.p_简答_total
			  this.docProperties.简答_total=this.p_简答_total
			break; 

			case "p_单选_order":
			  this.showProperties.题目顺序.单选=this.p_单选_order
			  this.docProperties.单选_order=this.p_单选_order
			break; 
			case "p_多选_order":
			  this.showProperties.题目顺序.多选=this.p_多选_order
			  this.docProperties.多选_order=this.p_多选_order
			break; 
			case "p_不定项_order":
			  this.showProperties.题目顺序.不定项=this.p_不定项_order
			  this.docProperties.不定项_order=this.p_不定项_order
			break; 
			case "p_填空_order":
			  this.showProperties.题目顺序.填空=this.p_填空_order
			  this.docProperties.填空_order=this.p_填空_order
			break; 
			case "p_判断_order":
			  this.showProperties.题目顺序.判断=this.p_判断_order
			  this.docProperties.判断_order=this.p_判断_order
			break; 
			case "p_简答_order":
			  this.showProperties.题目顺序.简答=this.p_简答_order
			  this.docProperties.简答_order=this.p_简答_order
			break;

			case "p_单选_random":
			  this.showProperties.题目数量.单选=this.p_单选_random
			  this.docProperties.单选_random=this.p_单选_random
			break; 
			case "p_多选_random":
			  this.showProperties.题目数量.多选=this.p_多选_random
			  this.docProperties.多选_random=this.p_多选_random
			break; 
			case "p_不定项_random":
			  this.showProperties.题目数量.不定项=this.p_不定项_random
			  this.docProperties.不定项_random=this.p_不定项_random
			break; 
			case "p_填空_random":
			  this.showProperties.题目数量.填空=this.p_填空_random
			  this.docProperties.填空_random=this.p_填空_random
			break; 
			case "p_判断_random":
			  this.showProperties.题目数量.判断=this.p_判断_random
			  this.docProperties.判断_random=this.p_判断_random
				break; 
			case "p_简答_random":
			  this.showProperties.题目数量.简答=this.p_简答_random
			  this.docProperties.简答_random=this.p_简答_random
				break; 
			case "p_tempdatabase":
			  this.databaseProperties.缓存文件夹=this.p_tempdatabase
			  this.docProperties.tempdatabase=this.p_tempdatabase
				break;
			case "p_database":
			  this.databaseProperties.试题文件夹=this.p_database
			  this.docProperties.database=this.p_database
			  break;
			case "p_avoidused":
			  this.showProperties.不选已用题目=this.p_avoidused
			  this.docProperties.avoid_used_title=this.p_avoidused
				break;
			case "p_merge_per":
				this.docProperties.单选_merge_per=this.p_merge_per
			  this.docProperties.简答_merge_per=this.p_merge_per
			  this.docProperties.判断_merge_per=this.p_merge_per
			  this.docProperties.填空_merge_per=this.p_merge_per
			  this.docProperties.不定项_merge_per=this.p_merge_per
			  this.docProperties.未知_merge_per=this.p_merge_per
			  this.docProperties.多选_merge_per=this.p_merge_per
				break;
			default:
			  break;
		  }
			this.updateServerProperties();
		},
		saveDocProperties:function(){
			var json={"saveDocProperties":{}};
		  this.createpaperresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.createpaperresult="配置保存成功 ："+data.info;         
			  },
			  error:function(data){
				tiku.$data.createpaperresult="配置保存失败 "+data.responseJSON.error; 
				console.log(data.responseJSON.error);
			  }
		  });
		},
		clearUsedFlag:function(){
		  var json={"clearUsedFlag":{}};
		  this.createpaperresult="执行中....";
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.createpaperresult="标识清除成功 ："+data.info;         
			  },
			  error:function(data){
				tiku.$data.createpaperresult="标识清除失败 "+data.responseJSON.error; 
				console.log(data.responseJSON.error);
			  }
		  });
		},
		updateServerProperties:function(){
		  var json={"updateDocProperties":this.docProperties};
		  this.createpaperresult="执行中....";
		  var vdata=this.data;
		  $.ajax({
			  type: "post",
			  dataType: "json",
			  //contentType:"application/json;charset=utf-8",
			  url:  "api",
			  data: JSON.stringify(json),
			  async:false,
			  success: function (data) {
				tiku.$data.createpaperresult="配置更新成功 ："+data.info;         
			  },
			  error:function(data){
				tiku.$data.createpaperresult="配置更新失败 "+data.responseJSON.error;; 
				console.log(data.responseJSON.error);
			  }
		  });
		},
		aaa:function(){
		  $.ajax({
			  /*
				  type: 请求的类型
				  dataType: 从服务端接收的数据类型 html, xml, text, json
				  url: 请求路径
				  data: 请求参数
				  success: 回调函数 msg: 从服务端接收过来的内容
			   */
			  type: "post",
			  dataType: "json",
			  url:  "api",
			  data: $(textarea1).val(),

			  success: function (msg) {
				  $(textarea2).val(msg.hello);
				  alert(msg.hello);
				  
			  }
		  });
		},
		bbb:function(){
		  $(textarea2).val( window.location.protocol+"://"+window.location.host+"/api");
		}
  }
});

(function(){
    tiku.FInitProperties();         
    tiku.FSetShow();
	tiku.FgetTemplateList();
	tiku.setVisabled("Vchouti");
	tiku.$data.showchoutifilename= "抓取试卷池指定试卷：";
	$("#loadingModal").modal('hide');
	}	
)()


