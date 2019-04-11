var tiku = new Vue({
	el: '#vueEntry',
	data: {
		navAppend: '隐藏',

		quchong: '重复题处理',
		guanli: '库管理',
		peizhi: '配置文件内容',
		createPapers: '生成试卷',
		chouti: '试卷抓题',
		classify: '试题分类',

		docProperties: "",
		databaseProperties: {},
		showProperties: {},
		VcreatePapers: false,
		Vchouti: true,
		Vquchong: false,
		Vguanli: false,
		Vpeizhi: false,
		Vclassify: false,

		quchongresult: "",

		choutifilename: "",
		showchoutifilename: "",
		choutiresult: "",

		createpaperresult: "",

		progressmask: false,

		navshow: true,

		p_merge_per: "100",

		p_tempdatabase: '', p_database: '',
		p_school: '', p_semester: '', p_subject: '', p_class: '', p_time: '',
		p_template: '', p_file_prefix: '', p_diffcopies: '',
		p_单选_total: '', p_多选_total: '', p_不定项_total: '', p_填空_total: '', p_判断_total: '', p_简答_total: '',
		p_单选_order: '', p_多选_order: '', p_不定项_order: '', p_填空_order: '', p_判断_order: '', p_简答_order: '',
		p_单选_random: '', p_多选_random: '', p_不定项_random: '', p_填空_random: '', p_判断_random: '', p_简答_random: '',
		p_avoidused: '',

		templatelist: [{ code: "templateA3.docx", name: 'A3' }, { code: "templateA4.docx", name: 'A4' }],
		avoidusedcontrol: [{ code: "true", name: '是' }, { code: "false", name: '否' }],

		guanliControl: "全部",
		guanliControlList: ["全部", "单选", "多选", "填空", "判断", "简答", "不定项", "未知"],

		guanliContent: "all",
		guanliContentList: [{ code: "all", name: "全部" }, { code: "OnlyWithoutAnswer", name: "只显示无答案问题" }, { code: "OnlyWithAnswer", name: "只显示含答案问题" }],

		guanliDocxIndex: 0,
		guanliDocxMaxIndex: 0,
		guanliresult: "",
		guanliMove: "单选",
		guanliMoveList: ["单选", "多选", "填空", "判断", "简答", "不定项"],
		guanliConform: "",
		guanliConformList: ["是", "否"],

		guanliAction: "",
		guanliMsg: "",

		guanliActionShow: "",
		Vconform: false,

		docxList: {},
		docxListCount: [{ code: "单选", value: 0 },
		{ code: "多选", value: 0 },
		{ code: "填空", value: 0 },
		{ code: "判断", value: 0 },
		{ code: "简答", value: 0 },
		{ code: "不定项", value: 0 },
		{ code: "未知", value: 0 }],
		currentDocx: "",
		currentDocxShow: "",

		p_background:"",
		p_backgroundlist:[]
	},
	methods: {

		shownav: function () {
			this.navshow = !this.navshow;
			if (!this.navshow) {
				this.navAppend = "显示";
			} else { this.navAppend = "隐藏"; }
		},
		setVisabled: function (type) {
			$(".leftNav").css("background-color", "");
			$("#" + type).css("background-color", "lightblue");
			this.Vquchong = false;
			this.Vguanli = false;
			this.Vpeizhi = false;
			this.VcreatePapers = false;
			this.Vchouti = false;
			this.Vclassify = false;
			switch (type) {
				case "Vquchong":
					this.Vquchong = true;
					break;
				case "Vguanli":
					this.Vguanli = true;
					break;
				case "Vpeizhi":
					this.Vpeizhi = true;
					break;
				case "VcreatePapers":
					this.VcreatePapers = true;
					break;
				case "Vchouti":
					this.Vchouti = true;
					break;
				case "Vclassify":
					this.Vclassify = true;
					break;

			}
		},

		FInitProperties: function () {
			var json = { "properties": {} };
			if (tiku.$data.docProperties == "") {
				$.ajax({
					type: "get",
					dataType: "json",
					contentType: "application/json;charset=utf-8",
					//url:  "api",
					url: "api?command=docProperties",
					data: JSON.stringify(json),
					async: false,
					success: function (data) {

						tiku.$data.docProperties = data;
						tiku.$data.showProperties.卷首 = {};
						tiku.$data.showProperties.试卷规格 = {};
						tiku.$data.showProperties.题目顺序 = {};
						tiku.$data.showProperties.题目总分 = {};
						tiku.$data.showProperties.题目数量 = {};

					},
					error: function (data) {
						console.log(data);
					}
				});
			}
		},
		FSetShow: function () {
			this.databaseProperties.缓存文件夹 = this.docProperties.tempdatabase
			this.databaseProperties.试题文件夹 = this.docProperties.database
			this.showProperties.卷首.学校 = this.docProperties.school
			this.showProperties.卷首.学期 = this.docProperties.semester
			this.showProperties.卷首.学科 = this.docProperties.subject
			this.showProperties.卷首.班级 = this.docProperties.class
			this.showProperties.卷首.考试时间 = this.docProperties.time

			this.showProperties.试卷规格.母版 = this.docProperties.template
			this.p_template = this.docProperties.template
			this.showProperties.试卷规格.文件名 = this.docProperties.file_prefix
			this.showProperties.试卷规格.分卷数 = this.docProperties.diffcopies

			this.showProperties.题目总分.单选 = this.docProperties.单选_total
			this.showProperties.题目总分.多选 = this.docProperties.多选_total
			this.showProperties.题目总分.不定项 = this.docProperties.不定项_total
			this.showProperties.题目总分.填空 = this.docProperties.填空_total
			this.showProperties.题目总分.判断 = this.docProperties.判断_total
			this.showProperties.题目总分.简答 = this.docProperties.简答_total

			this.showProperties.题目顺序.单选 = this.docProperties.单选_order
			this.showProperties.题目顺序.多选 = this.docProperties.多选_order
			this.showProperties.题目顺序.不定项 = this.docProperties.不定项_order
			this.showProperties.题目顺序.填空 = this.docProperties.填空_order
			this.showProperties.题目顺序.判断 = this.docProperties.判断_order
			this.showProperties.题目顺序.简答 = this.docProperties.简答_order


			this.showProperties.题目数量.单选 = this.docProperties.单选_random
			this.showProperties.题目数量.多选 = this.docProperties.多选_random
			this.showProperties.题目数量.不定项 = this.docProperties.不定项_random
			this.showProperties.题目数量.填空 = this.docProperties.填空_random
			this.showProperties.题目数量.判断 = this.docProperties.判断_random
			this.showProperties.题目数量.简答 = this.docProperties.简答_random

			this.showProperties.不选已用题目 = this.docProperties.avoid_used_title
			this.p_avoidused = this.docProperties.avoid_used_title

			this.p_merge_per = this.docProperties.单选_merge_per

			this.p_background = this.docProperties.backgroupImage

		},
		FNav: function (type) {
			this.FInitProperties();
			this.setVisabled(type);
		},
		FgetBackgroundList: function () {
			var json = { "properties": {} };
			$.ajax({
				type: "get",
				dataType: "json",
				contentType: "application/json;charset=utf-8",
				url: "api?command=getBackgroundList",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.p_backgroundlist = data.backgroundlist;
				},
				error: function (data) {
					console.log(data);
				}
			});
		},		
		FgetTemplateList: function () {
			var json = { "properties": {} };
			$.ajax({
				type: "get",
				dataType: "json",
				contentType: "application/json;charset=utf-8",
				url: "api?command=getTemplateName",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.templatelist = data.template;
				},
				error: function (data) {
					console.log(data);
				}
			});
		},
		changefilename: function () {
			this.choutifilename = $("#input-b1").val();
			this.choutifilename = this.choutifilename.split('fakepath')[1].replace('\\', '');
			this.showchoutifilename = "抓取试卷池指定试卷：" + this.choutifilename;
		},
		quchongfunc: function () {
			var json = { "quchong": {} };
			this.choutiresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.quchongresult = tiku.$data.choutifilename + ":" + data.info;
				},
				error: function (data) {
					tiku.$data.quchongresult = tiku.$data.choutifilename + " ---- " + data.responseJSON.error;
				}
			});
		},
		choutifuncBatch: function () {
			var json = { "pickupBatch": {} };
			this.choutiresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.choutiresult = tiku.$data.choutifilename + ":" + data.info;
				},
				error: function (data) {
					tiku.$data.choutiresult = tiku.$data.choutifilename + " ---- " + data.responseJSON.error;
				}
			});
		},
		choutifunc: function () {
			var json = { "pickup": { "path": this.choutifilename } };
			if (this.choutifilename == "") {
				this.createpaperresult = "请先点击Browse，选择需要从中抓题的试卷。";
				alert("请先点击Browse，选择需要从中抓题的试卷。");
				this.choutiresult = "未选择任何试卷，请先点击 '选择文件/Browse'，选择需要从中抓题的试卷。"
				return;
			}
			this.choutiresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.choutiresult = tiku.$data.choutifilename + ":" + data.info;
				},
				error: function (data) {
					tiku.$data.choutiresult = tiku.$data.choutifilename + " ---- " + data.responseJSON.error;
				}
			});
		},
		createpaperfunc: function () {
			$("#loadingModal").modal('show');
			this.progressmask = true;
			var json = { "createpapers": {} };
			this.createpaperresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.createpaperresult = "试卷生成成功 ：" + data.info;
				},
				error: function (data) {
					tiku.$data.createpaperresult = "试卷生成失败 " + data.responseJSON.error;;
					console.log(data.responseJSON.error);
				}
			});
			//this.progressmask=false;	  
			$("#loadingModal").modal('hide');
		},
		changeItem: function (name) {
			switch (name) {
				case "p_school":
					this.showProperties.卷首.学校 = this.p_school
					this.docProperties.school = this.p_school
					break;
				case "p_semester":
					this.showProperties.卷首.学期 = this.p_semester
					this.docProperties.semester = this.p_semester
					break;
				case "p_subject":
					this.showProperties.卷首.学科 = this.p_subject
					this.docProperties.subject = this.p_subject
					break;
				case "p_class":
					this.showProperties.卷首.班级 = this.p_class
					this.docProperties.class = this.p_class
					break;
				case "p_time":
					this.showProperties.卷首.考试时间 = this.p_time
					this.docProperties.time = this.p_time
					break;

				case "p_template":
					this.showProperties.试卷规格.母版 = this.p_template;
					this.docProperties.template = this.p_template;
					break;
				case "p_file_prefix":
					this.showProperties.试卷规格.文件名 = this.p_file_prefix
					this.docProperties.file_prefix = this.p_file_prefix
					break;
				case "p_diffcopies":
					this.showProperties.试卷规格.分卷数 = this.p_diffcopies
					this.docProperties.diffcopies = this.p_diffcopies
					break;

				case "p_单选_total":
					this.showProperties.题目总分.单选 = this.p_单选_total
					this.docProperties.单选_total = this.p_单选_total
					break;
				case "p_多选_total":
					this.showProperties.题目总分.多选 = this.p_多选_total
					this.docProperties.多选_total = this.p_多选_total
					break;
				case "p_不定项_total":
					this.showProperties.题目总分.不定项 = this.p_不定项_total
					this.docProperties.不定项_total = this.p_不定项_total
					break;
				case "p_填空_total":
					this.showProperties.题目总分.填空 = this.p_填空_total
					this.docProperties.填空_total = this.p_填空_total
					break;
				case "p_判断_total":
					this.showProperties.题目总分.判断 = this.p_判断_total
					this.docProperties.判断_total = this.p_判断_total
					break;
				case "p_简答_total":
					this.showProperties.题目总分.简答 = this.p_简答_total
					this.docProperties.简答_total = this.p_简答_total
					break;

				case "p_单选_order":
					this.showProperties.题目顺序.单选 = this.p_单选_order
					this.docProperties.单选_order = this.p_单选_order
					break;
				case "p_多选_order":
					this.showProperties.题目顺序.多选 = this.p_多选_order
					this.docProperties.多选_order = this.p_多选_order
					break;
				case "p_不定项_order":
					this.showProperties.题目顺序.不定项 = this.p_不定项_order
					this.docProperties.不定项_order = this.p_不定项_order
					break;
				case "p_填空_order":
					this.showProperties.题目顺序.填空 = this.p_填空_order
					this.docProperties.填空_order = this.p_填空_order
					break;
				case "p_判断_order":
					this.showProperties.题目顺序.判断 = this.p_判断_order
					this.docProperties.判断_order = this.p_判断_order
					break;
				case "p_简答_order":
					this.showProperties.题目顺序.简答 = this.p_简答_order
					this.docProperties.简答_order = this.p_简答_order
					break;

				case "p_单选_random":
					this.showProperties.题目数量.单选 = this.p_单选_random
					this.docProperties.单选_random = this.p_单选_random
					break;
				case "p_多选_random":
					this.showProperties.题目数量.多选 = this.p_多选_random
					this.docProperties.多选_random = this.p_多选_random
					break;
				case "p_不定项_random":
					this.showProperties.题目数量.不定项 = this.p_不定项_random
					this.docProperties.不定项_random = this.p_不定项_random
					break;
				case "p_填空_random":
					this.showProperties.题目数量.填空 = this.p_填空_random
					this.docProperties.填空_random = this.p_填空_random
					break;
				case "p_判断_random":
					this.showProperties.题目数量.判断 = this.p_判断_random
					this.docProperties.判断_random = this.p_判断_random
					break;
				case "p_简答_random":
					this.showProperties.题目数量.简答 = this.p_简答_random
					this.docProperties.简答_random = this.p_简答_random
					break;
				case "p_tempdatabase":
					this.databaseProperties.缓存文件夹 = this.p_tempdatabase
					this.docProperties.tempdatabase = this.p_tempdatabase
					break;
				case "p_database":
					this.databaseProperties.试题文件夹 = this.p_database
					this.docProperties.database = this.p_database
					break;
				case "p_avoidused":
					this.showProperties.不选已用题目 = this.p_avoidused
					this.docProperties.avoid_used_title = this.p_avoidused
					break;
				case "p_merge_per":
					this.docProperties.单选_merge_per = this.p_merge_per
					this.docProperties.简答_merge_per = this.p_merge_per
					this.docProperties.判断_merge_per = this.p_merge_per
					this.docProperties.填空_merge_per = this.p_merge_per
					this.docProperties.不定项_merge_per = this.p_merge_per
					this.docProperties.未知_merge_per = this.p_merge_per
					this.docProperties.多选_merge_per = this.p_merge_per
					break;
				case "p_background":
					this.docProperties.backgroupImage=this.p_background
					setbackgroup();
					break;
				default:
					break;
			}
			this.updateServerProperties();
		},
		saveDocProperties: function () {
			var json = { "saveDocProperties": {} };
			this.createpaperresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.createpaperresult = "配置保存成功 ：" + data.info;
				},
				error: function (data) {
					tiku.$data.createpaperresult = "配置保存失败 " + data.responseJSON.error;
					console.log(data.responseJSON.error);
				}
			});
		},
		clearUsedFlag: function () {
			var json = { "clearUsedFlag": {} };
			this.createpaperresult = "执行中....";
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.createpaperresult = "标识清除成功 ：" + data.info;
				},
				error: function (data) {
					tiku.$data.createpaperresult = "标识清除失败 " + data.responseJSON.error;
					console.log(data.responseJSON.error);
				}
			});
		},
		updateServerProperties: function () {
			var json = { "updateDocProperties": this.docProperties };
			this.createpaperresult = "执行中....";
			var vdata = this.data;
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.createpaperresult = "配置更新成功 ：" + data.info;
				},
				error: function (data) {
					tiku.$data.createpaperresult = "配置更新失败 " + data.responseJSON.error;;
					console.log(data.responseJSON.error);
				}
			});
		},
		initdocxList: function () {
			if (typeof (this.docxList.单选) == "undefined") {
				this.docxList.单选 = {}
			}
			if (typeof (this.docxList.多选) == "undefined") {
				this.docxList.多选 = {}
			}
			if (typeof (this.docxList.不定项) == "undefined") {
				this.docxList.不定项 = {}
			}
			if (typeof (this.docxList.判断) == "undefined") {
				this.docxList.判断 = {}
			}
			if (typeof (this.docxList.填空) == "undefined") {
				this.docxList.填空 = {}
			}
			if (typeof (this.docxList.简答) == "undefined") {
				this.docxList.简答 = {}
			}
			if (typeof (this.docxList.未知) == "undefined") {
				this.docxList.未知 = {}
			}
		},
		preshowTitle: function (type, index) {
			this.currentDocx = this.docxList[type][index];
			this.currentDocxShow = "第" + (this.guanliDocxIndex + 1) + "题." + this.docxList[type][index];
			if (this.currentDocx != "") {
				//this.guanliresult = "加载文件" + this.currentDocx;
				this.showtitle(this.currentDocx);
			}
		},
		prevTitle: function () {
			this.guanliAction = "";
			this.guanliDocxIndex = this.guanliDocxIndex - 1;
			if (this.guanliDocxIndex < 0) {
				this.guanliDocxIndex = 0;
				this.guanliresult = "已经到[" + this.guanliControl + "]开头";
			} else {
				this.showDocx()
			}
		},
		nextTitle: function () {
			this.guanliAction = "";
			this.guanliDocxIndex = this.guanliDocxIndex + 1;
			if (this.guanliControl == "全部") {
				if (this.guanliDocxIndex > this.guanliDocxMaxIndex) {
					this.guanliDocxIndex = this.guanliDocxMaxIndex - 1;
					this.guanliresult = "已经到[" + this.guanliControl + "]末尾";
				} else {
					this.showDocx()
				}
			} else {
				if (this.guanliDocxIndex >= this.docxList[this.guanliControl].length) {
					this.guanliDocxIndex = this.docxList[this.guanliControl].length - 1;
					this.guanliresult = "已经到[" + this.guanliControl + "]末尾";
				} else {
					this.showDocx()
				}
			}
		},
		getMaxIndex: function () {
			var type = ["单选", "多选", "填空", "判断", "简答", "不定项", "未知"]
			this.guanliDocxMaxIndex = 0
			var mv = tiku.$data
			$.each(type, function (index, value) {
				mv.guanliDocxMaxIndex = mv.guanliDocxMaxIndex + mv.docxList[value].length;
			});
		},
		showDocx: function () {
			if (this.guanliControl != "全部") {
				if (this.docxList[this.guanliControl].length > this.guanliDocxIndex && this.guanliDocxIndex >= 0) {
					this.preshowTitle(this.guanliControl, this.guanliDocxIndex);
				}
			} else {
				var showindex = this.guanliDocxIndex
				var type = ["单选", "多选", "填空", "判断", "简答", "不定项", "未知"]
				var mv = tiku.$data
				var tv = tiku
				$.each(type, function (index, value) {
					if (showindex < mv.docxList[value].length) {
						tv.preshowTitle(value, showindex);
						return false;
					} else {
						showindex = showindex - mv.docxList[value].length;
					}
				});
			}
		},
		clearAera() {
			$(textarea1).val("");
			$(textarea2).val("");
		},

		getDocxInfo: function (pos) {
			this.clearAera();
			this.resetGetDocxInfo(pos);
			this.getDocxList();
			this.docxListCount = [{ code: "单选", value: this.docxList.单选.length },
			{ code: "多选", value: this.docxList.多选.length },
			{ code: "填空", value: this.docxList.填空.length },
			{ code: "判断", value: this.docxList.判断.length },
			{ code: "简答", value: this.docxList.简答.length },
			{ code: "不定项", value: this.docxList.不定项.length },
			{ code: "未知", value: this.docxList.未知.length }]
			this.getMaxIndex();
			this.showDocx();
		},
		resetGetDocxInfo: function (pos) {
			this.guanliDocxIndex = 0;
			if (typeof(pos)!="undefined"){
				this.guanliDocxIndex=pos;
				if (this.guanliDocxIndex<0) this.guanliDocxIndex=0;
			}
			this.guanliDocxMaxIndex = 0;
			//this.guanliresult = "";

			this.docxList = {};
			this.docxListCount = [];
			this.currentDocx = "";
			this.currentDocxShow = "";

			this.guanliActionShow = "";
			this.Vconform = false;
			this.guanliConform = "";

			this.guanliAction = "";
			this.guanliMsg = "";
		},
		guanliDoAction:function(pos){			
			var file=this.currentDocx;
			var newfile=this.replaceAll(this.currentDocx,this.guanliControl,this.guanliMove);
			var titleword=$(textarea1).val();
			var answerword=$(textarea2).val();
			var word={"filename":file,"title":titleword,"answer":answerword,"moveto":newfile};
			var json = {};
			json[this.guanliAction]=word;
			this.guanliresult = "执行中....";
			$.ajax({
				type: "post",
				dataType: "json",
				//contentType:"application/json;charset=utf-8",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.guanliresult = data.info;
				},
				error: function (data) {
					tiku.$data.guanliresult = "执行失败 " + data.responseJSON.error;
					console.log(data.responseJSON.error);
				}
			});
			//this.guanliresult="已经"+this.guanliMsg;
			//if (this.guanliAction == "save") this.currentDocxShow = this.currentDocx;
			this.guanliAction = "";
			this.getDocxInfo(pos);
		},
		newTitle: function () {
			if (this.guanliControl != "全部") {
				this.clearAera();
				this.guanliAction = "new";
				this.clearAera();
				this.guanliresult = "新增";
				this.currentDocx = "试题库/" + this.guanliControl + "/" + Date.parse(new Date()) + ".docx";
				this.currentDocxShow = "准备新增 " + this.currentDocx + " ，请保存前不要更换题目或者刷新，那样会丢失新增内容";
			} else {
				this.guanliresult = "新增操作题型无效，请不要选择‘全部’";
			}
		},
		saveTitle: function () {
			this.guanliAction = "save";
			this.guanliMsg = "保存 " + this.currentDocx;
			if ($("#fastconfirm")[0].checked) {
				this.guanliDoAction(this.guanliDocxIndex);
			} else {
				this.guanliActionShow = this.guanliMsg;
				this.Vconform = true;
			}
		},
		delTitle: function () {
			if (this.guanliAction == "new"){
				this.guanliresult = "请先完成 " + this.currentDocx +"存档";
				return 
			}
			this.guanliAction = "del";
			this.guanliMsg = "删除 " + this.currentDocx;
			if ($("#fastconfirm")[0].checked) {
				this.guanliDoAction(this.guanliDocxIndex-1);
			} else {
				this.guanliActionShow =this.guanliMsg;
				this.Vconform = true;
			}
		},
		moveTitle: function () {
			if (this.guanliAction == "new"){
				this.guanliresult = "请先完成 " + this.currentDocx +"存档";
				return 
			}
			if (this.guanliControl==this.guanliMove){
				this.guanliresult = "目标相同，无须变动";
				return 
			}
			this.guanliAction = "move";
			this.guanliMsg = "移动 " + this.currentDocx + "到" + this.guanliMove;
			if ($("#fastconfirm")[0].checked) {
				this.guanliDoAction(this.guanliDocxIndex-1);
			} else {
				this.guanliActionShow = this.guanliMsg;
				this.Vconform = true;
			}
		},
		conform: function () {
			if (this.guanliConform=="是") {
				if (this.guanliAction != "save"){
					this.guanliDoAction(this.guanliDocxIndex-1);
				}else{
					this.guanliDoAction(this.guanliDocxIndex);	
				}
			}
			this.Vconform = false;			
		},
		getDocxList: function () {
			var json = { "docxList": { "type": this.guanliControl, "answer": this.guanliContent } };
			this.initdocxList();
			$.ajax({
				type: "post",
				dataType: "json",
				contentType: "application/json;charset=utf-8",
				//url:  "api",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					tiku.$data.docxList.单选 = data.单选;
					tiku.$data.docxList.多选 = data.多选;
					tiku.$data.docxList.不定项 = data.不定项;
					tiku.$data.docxList.判断 = data.判断;
					tiku.$data.docxList.填空 = data.填空;
					tiku.$data.docxList.简答 = data.简答;
					tiku.$data.docxList.未知 = data.未知;
				},
				error: function (data) {
					tiku.$data.guanliresult = "执行失败 " + data.responseJSON.error;
				}
			});
		},
		replaceAll: function (a, b, c) {
			var d = a.replace(b, c);
			for (i = 0; i < 10; i++) {
				d = d.replace(b, c);
			}
			return d;
		},
		showtitle: function (docpath) {
			var json = { "docxTitle": docpath };
			this.initdocxList();
			$.ajax({
				type: "post",
				dataType: "json",
				contentType: "application/json;charset=utf-8",
				//url:  "api",
				url: "api",
				data: JSON.stringify(json),
				async: false,
				success: function (data) {
					$(textarea1).val(data.title);
					$(textarea2).val(data.answer);
				},
				error: function (data) {
					tiku.$data.guanliresult = "执行失败 " + data.responseJSON.error;
				}
			});
		},
		aaa: function () {
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
				url: "api",
				data: $(textarea1).val(),

				success: function (msg) {
					$(textarea2).val(msg.hello);
					alert(msg.hello);

				}
			});
		},
		bbb: function () {
			$(textarea2).val(window.location.protocol + "://" + window.location.host + "/api");
		}
	}
});

var setbackgroup = function () {
	var div_h = $(window).height() - 50;
	$("#chouti").height(div_h);
	$("#quchong").height(div_h);
	$("#createPapers").height(div_h);
	$("#guanli").height(div_h);
	$("#classify").height(div_h);
	$("#peizhi").height(div_h);
	if (tiku.$data.p_background != "") {
		$("body").css("background", "url(./themes/backgroup/" + tiku.$data.p_background + ")").css("background-repeat", "no-repeat").css("background-size", "100% 100%");
	}

};

(function () {
	tiku.FInitProperties();
	tiku.FSetShow();
	tiku.FgetTemplateList();
	tiku.FgetBackgroundList();
	tiku.setVisabled("Vchouti");
	tiku.$data.showchoutifilename = "抓取试卷池指定试卷：";
	$("#loadingModal").modal('hide');
	setbackgroup();
}
)()

/*
div.background{
	position:absolute;
	width:100%;
	height:100%;
	background-image:url(timg.jpg);
	background-repeat:no-repeat;
	background-size:100% 100%;

}
*/