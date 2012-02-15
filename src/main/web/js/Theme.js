/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

IS_Portal.theme = {
  defaultTheme: {
	background:{image: "./skin/imgs/theme/stripe.png"},
	widget:{
	  header:{
		background:{image: "./skin/imgs/theme/widget_header.png"}
	  },
	  border:{color:"#BCBCBC",radius: '0px'},
	  shade:{color:"#EEE"},
	  subheader:{
		background:{color:"#EEEEFF"}
	  }
	}
  },
	
  currentTheme:{},
	
  backgroundImages: [
	  "./skin/imgs/theme/tartan_01.png",
	  "./skin/imgs/theme/tartan_02.png",
	  "./skin/imgs/theme/tartan_03.png",
	  "./skin/imgs/theme/border_01.png",
	  "./skin/imgs/theme/stripe_01.png",
	  "./skin/imgs/theme/bg006_05.gif",
	  "./skin/imgs/theme/bg055_02.gif",
	  "./skin/imgs/theme/bg056_02.gif",
	  "./skin/imgs/theme/bg057_04.gif",
	  "./skin/imgs/theme/bg058_04.gif",
	  "./skin/imgs/theme/bg073_09.gif",
	  "./skin/imgs/theme/bg074_09.gif"
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
	  "#FFE4E1",
	  "#FFDAB9",
	  "#FFF5EE",
	  "#FFEFD5",
	  "#FFFFE0",
	  "#F0FFF0",
	  "#F5FFFA",
	  "#E0FFFF",
	  "#F0F8FF",
	  "#F8F8FF",
	  "#FFF0F5",
	  "#FFFAFA",
	  "#F5F5F5"
	  ],
	
  _setBackground: function(opt){
	  if(!opt)
		  opt = this.defaultTheme['background'];
	  
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
	  if(!opt)
		  opt = this.defaultTheme['background'];
	  
	  var saveOpt = $H({});
	  for( i in opt){
		  if( i == 'color' || i == 'image' || i == 'repeat' || i == 'position' || i == 'attachment')
			saveOpt.set(i, opt.i);
	  }
	  try{
		  this._setBackground(opt);
	  }catch(e){alert(e);}
	  //Send to Server
	  if(saveOpt.size == 0)
		this.currentTheme['background'] = this.defaultTheme['background'];
	  else
		this.currentTheme['background'] = opt;
	  this.saveTheme()
  },
	
  _setWidgetTheme: function(opt){
	  if(!opt)
		  opt = this.defaultTheme.widget;

	  var widgetHeaderStyle = [];
	  var subWidgetHeaderStyle = [];
	  var widgetBoxStyle = [];
	  var widgetShadeStyle = [];
	  if(opt.header && opt.header.background && opt.header.background.image){
		  widgetHeaderStyle.push('background-image:url(' + opt.header.background.image + ')');
	  }
	  if(opt.subheader && opt.subheader.background && opt.subheader.background.color){
		  subWidgetHeaderStyle.push('background-color:' + opt.subheader.background.color);
	  }
	  
	  if(opt.border){
		  if(opt.border.none){
			  widgetBoxStyle.push('border:none');
		  }else{
			  widgetBoxStyle.push('border-style:solid;border-width:1px;border-color:' + this.defaultTheme.widget.border.color);
		  }
		  if(opt.border.radius){
			  widgetBoxStyle.push('border-radius:' + opt.border.radius);
			  widgetBoxStyle.push('-webkit-border-radius:' + opt.border.radius);
			  widgetBoxStyle.push('-moz-border-radius:' + opt.border.radius);
			  
			  widgetShadeStyle.push('border-radius:' + opt.border.radius);
			  widgetShadeStyle.push('-webkit-border-radius:' + opt.border.radius);
			  widgetShadeStyle.push('-moz-border-radius:' + opt.border.radius);
			  
			  //widgetHeaderStyle.push('border-radius:' + opt.border.radius);
			  widgetHeaderStyle.push('-moz-border-radius-topleft:' + opt.border.radius);
			  widgetHeaderStyle.push('-moz-border-radius-topright:' + opt.border.radius);
		  }
	  }
	  if(widgetHeaderStyle.length || subWidgetHeaderStyle.length || widgetBoxStyle.length){
		  is_addCssRule('.infoScoop .widget .widgetHeader', widgetHeaderStyle.join(';') + ';');
		  subWidgetHeaderStyle.push('background-image:none');
		  subWidgetHeaderStyle.push('-moz-border-radius-topleft: 0px; -moz-border-radius-topright: 0px');
		  is_addCssRule('.infoScoop .subWidget .widgetHeader', subWidgetHeaderStyle.join(';') + ';');
		  is_addCssRule('.infoScoop .widget .widgetBox', widgetBoxStyle.join(';') + ';');
		  is_addCssRule('.infoScoop .widget .widgetShade', widgetShadeStyle.join(';') + ';');
		  is_addCssRule('.infoScoop .subwidget .widgetBox', 'border:none;-webkit-border-radius:0;-moz-border-radius:0;');
		  is_addCssRule('.widgetBoxNoHeader', widgetBoxStyle.join(';') + ';');
	  }
  },

  changeWidgetTheme: function(opt){
	  if(!opt)
		  opt = this.defaultTheme.widget;
	  
	  this._setWidgetTheme(opt);

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
	  if(!opt)
		opt = this.defaultTheme;

	  this._setBackground(opt.background);
	  
	  this._setWidgetTheme(opt.widget);
  }
};
