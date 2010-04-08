IS_Portal.theme = {
  defaultTheme: {
	background:{image: "./skin/imgs/stripe.png"},
	widget:{
	  header:{
		background:{image: "./skin/imgs/widget_header.png"}
	  },
	  border:{color:"#BCBCBC"},
	  shade:{color:"#EEE"},
	  subheader:{
		background:{color:"#EEEEFF"}
	  }
	}
  },
 
  backgroundImages: [
	  "./skin/imgs/theme/tartan_01.png",
	  "./skin/imgs/theme/tartan_02.png",
	  "./skin/imgs/theme/tartan_03.png",
	  "./skin/imgs/theme/border_01.png",
	  "./skin/imgs/theme/stripe_01.png",
	  "./skin/imgs/theme/border_02.png",
	  "./skin/imgs/theme/japan_01.jpg",
	  "./skin/imgs/theme/flower.gif"
	  ],
	
  widgetHeaderImages: [
	  "./skin/imgs/theme/header_blue.png",
	  "./skin/imgs/theme/header_red.png",
	  "./skin/imgs/theme/header_green.png",
	  "./skin/imgs/theme/header_yellow.png",
	  "./skin/imgs/theme/header_circle_blue.jpg",
	  "./skin/imgs/theme/header_circle_yellow.jpg"
	  ],

  subWidgetHeaderColors: [
	  "#f0fff0",
	  "#fdf5e6",
	  "#f0ffff",
	  "#f5f5dc",
	  "#fff0f5",
	  "#f8f8ff"
	  ],
	
  setBackground: function(opt){
	  var bodyStyle = document.body.style;
	  
	  if(opt.color)
		bodyStyle.backgroundColor = opt.color;
	  
	  if(opt.image)
		bodyStyle.backgroundImage = 'url(' + opt.image + ')';

	  if(opt.repeat)
		bodyStyle.backgroundRepeat = opt.repeat;
	
	  if(opt.position)
		bodyStyle.backgroundPosition = opt.position;
	  
	  if(opt.attachment)
		bodyStyle.backgroundAttachment = opt.attachment;
  },

  changeBackground: function(opt){
	  var saveOpt = $H({});
	  for( i in opt){
		  if( i == 'color' || i == 'image' || i == 'repeat' || i == 'position' || i == 'attachment')
			saveOpt.set(i, opt.i);
	  }
	  try{
		  this.setBackground(opt);
	  }catch(e){alert(e);}
	  //Send to Server
	  if(saveOpt.size == 0)
		this.currentTheme['background'] = this.defaultTheme['background'];
	  else
		this.currentTheme['background'] = opt;
	  this.saveTheme()
  },
	
  setWidgetTheme: function(opt){
	
	  if(opt.header){
		  if(opt.header.background.image){
			  is_addCssRule('.infoScoop .widget .widgetHeader', 'background-image:url(' + opt.header.background.image + ')');
			  is_addCssRule('.infoScoop .subWidget .widgetHeader', 'background-image:none');
		  }
	  }
	  if(opt.subheader){
		  if(opt.subheader.background.color){
			  is_addCssRule('.infoScoop .subWidget .widgetHeader', 'background-color:' + opt.subheader.background.color);
		  }
	  }
	  if(opt.border){
		  if(opt.border.none){
			  is_addCssRule('.infoScoop .widget .widgetBox', 'border:none');
			  is_addCssRule('.infoScoop .widget .widgetShade', 'border:none');
		  }else{
			  is_addCssRule('.infoScoop .widget .widgetBox', 'border-style:solid;;border-width:0 1px 1px 1px;border-color:' + this.defaultTheme.widget.border.color);
			  is_addCssRule('.infoScoop .widget .widgetShade', 'border-style:solid;border-width:0 3px 4px 1px;border-color:' + this.defaultTheme.widget.shade.color);
			  is_addCssRule('.infoScoop .subWidget .widgetBox', 'border:none');
			  is_addCssRule('.infoScoop .subWidget .widgetShade', 'border:none');
		  }
	  }
  },

  changeWidgetTheme: function(opt){
	  this.setWidgetTheme(opt);

	  this.currentTheme['widget'] = opt;
	  this.saveTheme();
  },

  saveTheme: function(){
	  //Send to Server
	  var opt = this.currentTheme;
	  if($H(opt).size == 0)
		IS_Widget.setPreferenceCommand('theme', 'false');
	  else
		IS_Widget.setPreferenceCommand('theme', Object.toJSON(opt));
  },

  setTheme: function(opt){
	  if(!opt){
		  this.currentTheme = this.defaultTheme;
		  opt = this.currentTheme;
	  }
	  
	  if(opt.background)
		this.setBackground(opt.background);
	  if(opt.widget)
		this.setWidgetTheme(opt.widget);
  }
}