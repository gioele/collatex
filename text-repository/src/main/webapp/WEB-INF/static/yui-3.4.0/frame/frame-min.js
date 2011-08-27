/*
YUI 3.4.0 (build 3928)
Copyright 2011 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add("frame",function(d){var a=function(){a.superclass.constructor.apply(this,arguments);},c=":last-child",b="body";d.extend(a,d.Base,{_ready:null,_rendered:null,_iframe:null,_instance:null,_create:function(e){var k,j,g,i;this._iframe=d.Node.create(a.HTML);this._iframe.setStyle("visibility","hidden");this._iframe.set("src",this.get("src"));this.get("container").append(this._iframe);this._iframe.set("height","99%");var f="",h=((this.get("extracss"))?'<style id="extra_css">'+this.get("extracss")+"</style>":"");f=d.substitute(a.PAGE_HTML,{DIR:this.get("dir"),LANG:this.get("lang"),TITLE:this.get("title"),META:a.META,LINKED_CSS:this.get("linkedcss"),CONTENT:this.get("content"),BASE_HREF:this.get("basehref"),DEFAULT_CSS:a.DEFAULT_CSS,EXTRA_CSS:h});if(d.config.doc.compatMode!="BackCompat"){f=a.getDocType()+"\n"+f;}else{}g=this._resolveWinDoc();g.doc.open();g.doc.write(f);g.doc.close();if(!g.doc.documentElement){var l=d.later(1,this,function(){if(g.doc&&g.doc.documentElement){e(g);l.cancel();}},null,true);}else{e(g);}},_resolveWinDoc:function(f){var e=(f)?f:{};e.win=d.Node.getDOMNode(this._iframe.get("contentWindow"));e.doc=d.Node.getDOMNode(this._iframe.get("contentWindow.document"));if(!e.doc){e.doc=d.config.doc;}if(!e.win){e.win=d.config.win;}return e;},_onDomEvent:function(h){var g,f;h.frameX=h.frameY=0;if(h.pageX>0||h.pageY>0){if(h.type.substring(0,3)!=="key"){f=this._instance.one("win");g=this._iframe.getXY();h.frameX=g[0]+h.pageX-f.get("scrollLeft");h.frameY=g[1]+h.pageY-f.get("scrollTop");}}h.frameTarget=h.target;h.frameCurrentTarget=h.currentTarget;h.frameEvent=h;this.fire("dom:"+h.type,h);},initializer:function(){this.publish("ready",{emitFacade:true,defaultFn:this._defReadyFn});},destructor:function(){var e=this.getInstance();e.one("doc").detachAll();e=null;this._iframe.remove();},_DOMPaste:function(i){var g=this.getInstance(),f="",h=g.config.win;if(i._event.originalTarget){f=i._event.originalTarget;}if(i._event.clipboardData){f=i._event.clipboardData.getData("Text");}if(h.clipboardData){f=h.clipboardData.getData("Text");if(f===""){if(!h.clipboardData.setData("Text",f)){f=null;}}}i.frameTarget=i.target;i.frameCurrentTarget=i.currentTarget;i.frameEvent=i;if(f){i.clipboardData={data:f,getData:function(){return f;}};}else{i.clipboardData=null;}this.fire("dom:paste",i);},_defReadyFn:function(){var e=this.getInstance();d.each(a.DOM_EVENTS,function(g,f){var h=d.bind(this._onDomEvent,this),i=((d.UA.ie&&a.THROTTLE_TIME>0)?d.throttle(h,a.THROTTLE_TIME):h);if(!e.Node.DOM_EVENTS[f]){e.Node.DOM_EVENTS[f]=1;}if(g===1){if(f!=="focus"&&f!=="blur"&&f!=="paste"){if(f.substring(0,3)==="key"){e.on(f,i,e.config.doc);}else{e.on(f,h,e.config.doc);}}}},this);e.Node.DOM_EVENTS.paste=1;e.on("paste",d.bind(this._DOMPaste,this),e.one("body"));e.on("focus",d.bind(this._onDomEvent,this),e.config.win);e.on("blur",d.bind(this._onDomEvent,this),e.config.win);e._use=e.use;e.use=d.bind(this.use,this);this._iframe.setStyles({visibility:"inherit"});e.one("body").setStyle("display","block");if(d.UA.ie){this._fixIECursors();}},_fixIECursors:function(){var h=this.getInstance(),f=h.all("table"),g=h.all("br"),e;if(f.size()&&g.size()){e=f.item(0).get("sourceIndex");g.each(function(l){var j=l.get("parentNode"),k=j.get("children"),i=j.all(">br");if(j.test("div")){if(k.size()>2){l.replace(h.Node.create("<wbr>"));}else{if(l.get("sourceIndex")>e){if(i.size()){l.replace(h.Node.create("<wbr>"));}}else{if(i.size()>1){l.replace(h.Node.create("<wbr>"));}}}}});}},_onContentReady:function(h){if(!this._ready){this._ready=true;var g=this.getInstance(),f=d.clone(this.get("use"));this.fire("contentready");if(h){g.config.doc=d.Node.getDOMNode(h.target);}f.push(d.bind(function(){if(g.Selection){g.Selection.DEFAULT_BLOCK_TAG=this.get("defaultblock");}if(this.get("designMode")){if(d.UA.ie){g.config.doc.body.contentEditable="true";this._ieSetBodyHeight();g.on("keyup",d.bind(this._ieSetBodyHeight,this),g.config.doc);}else{g.config.doc.designMode="on";}}this.fire("ready");},this));g.use.apply(g,f);g.one("doc").get("documentElement").addClass("yui-js-enabled");}},_ieHeightCounter:null,_ieSetBodyHeight:function(k){if(!this._ieHeightCounter){this._ieHeightCounter=0;}this._ieHeightCounter++;var j=false;if(!k){j=true;}if(k){switch(k.keyCode){case 8:case 13:j=true;break;}if(k.ctrlKey||k.shiftKey){j=true;}}if(j){try{var i=this.getInstance();var g=this._iframe.get("offsetHeight");var f=i.config.doc.body.scrollHeight;if(g>f){g=(g-15)+"px";i.config.doc.body.style.height=g;}else{i.config.doc.body.style.height="auto";}}catch(k){if(this._ieHeightCounter<100){d.later(200,this,this._ieSetBodyHeight);}else{}}}},_resolveBaseHref:function(e){if(!e||e===""){e=d.config.doc.location.href;if(e.indexOf("?")!==-1){e=e.substring(0,e.indexOf("?"));}e=e.substring(0,e.lastIndexOf("/"))+"/";}return e;},_getHTML:function(e){if(this._ready){var f=this.getInstance();e=f.one("body").get("innerHTML");}return e;},_setHTML:function(e){if(this._ready){var f=this.getInstance();f.one("body").set("innerHTML",e);}else{this.on("contentready",d.bind(function(g,i){var h=this.getInstance();h.one("body").set("innerHTML",g);},this,e));}return e;},_getLinkedCSS:function(e){if(!d.Lang.isArray(e)){e=[e];}var f="";if(!this._ready){d.each(e,function(g){if(g!==""){f+='<link rel="stylesheet" href="'+g+'" type="text/css">';}});}else{f=e;}return f;},_setLinkedCSS:function(e){if(this._ready){var f=this.getInstance();f.Get.css(e);}return e;},_setExtraCSS:function(e){if(this._ready){var g=this.getInstance(),f=g.one("#extra_css");f.remove();g.one("head").append('<style id="extra_css">'+e+"</style>");}return e;},_instanceLoaded:function(f){this._instance=f;this._onContentReady();var g=this._instance.config.doc;if(this.get("designMode")){if(!d.UA.ie){try{g.execCommand("styleWithCSS",false,false);g.execCommand("insertbronreturn",false,false);}catch(e){}}}},use:function(){var g=this.getInstance(),f=d.Array(arguments),e=false;if(d.Lang.isFunction(f[f.length-1])){e=f.pop();}if(e){f.push(function(){e.apply(g,arguments);
});}g._use.apply(g,f);},delegate:function(g,f,e,i){var h=this.getInstance();if(!h){return false;}if(!i){i=e;e="body";}return h.delegate(g,f,e,i);},getInstance:function(){return this._instance;},render:function(e){if(this._rendered){return this;}this._rendered=true;if(e){this.set("container",e);}this._create(d.bind(function(i){var k,l,f=d.bind(function(m){this._instanceLoaded(m);},this),h=d.clone(this.get("use")),g={debug:false,win:i.win,doc:i.doc},j=d.bind(function(){g=this._resolveWinDoc(g);k=YUI(g);try{k.use("node-base",f);if(l){clearInterval(l);}}catch(m){l=setInterval(function(){j();},350);}},this);h.push(j);d.use.apply(d,h);},this));return this;},_handleFocus:function(){var h=this.getInstance(),g=new h.Selection();if(g.anchorNode){var j=g.anchorNode,i;if(j.test("p")&&j.get("innerHTML")===""){j=j.get("parentNode");}i=j.get("childNodes");if(i.size()){if(i.item(0).test("br")){g.selectNode(j,true,false);}else{if(i.item(0).test("p")){j=i.item(0).one("br.yui-cursor");if(j){j=j.get("parentNode");}if(!j){j=i.item(0).get("firstChild");}if(!j){j=i.item(0);}if(j){g.selectNode(j,true,false);}}else{var e=h.one("br.yui-cursor");if(e){var f=e.get("parentNode");if(f){g.selectNode(f,true,false);}}}}}}},focus:function(e){if(d.UA.ie&&d.UA.ie<9){try{d.one("win").focus();this.getInstance().one("win").focus();}catch(g){}if(e===true){this._handleFocus();}if(d.Lang.isFunction(e)){e();}}else{try{d.one("win").focus();d.later(100,this,function(){this.getInstance().one("win").focus();if(e===true){this._handleFocus();}if(d.Lang.isFunction(e)){e();}});}catch(f){}}return this;},show:function(){this._iframe.setStyles({position:"static",left:""});if(d.UA.gecko){try{this._instance.config.doc.designMode="on";}catch(f){}this.focus();}return this;},hide:function(){this._iframe.setStyles({position:"absolute",left:"-999999px"});return this;}},{THROTTLE_TIME:100,DOM_EVENTS:{dblclick:1,click:1,paste:1,mouseup:1,mousedown:1,keyup:1,keydown:1,keypress:1,activate:1,deactivate:1,beforedeactivate:1,focusin:1,focusout:1},DEFAULT_CSS:"body { background-color: #fff; font: 13px/1.22 arial,helvetica,clean,sans-serif;*font-size:small;*font:x-small; } a, a:visited, a:hover { color: blue !important; text-decoration: underline !important; cursor: text !important; } img { cursor: pointer !important; border: none; }",HTML:'<iframe border="0" frameBorder="0" marginWidth="0" marginHeight="0" leftMargin="0" topMargin="0" allowTransparency="true" width="100%" height="99%"></iframe>',PAGE_HTML:'<html dir="{DIR}" lang="{LANG}"><head><title>{TITLE}</title>{META}<base href="{BASE_HREF}"/>{LINKED_CSS}<style id="editor_css">{DEFAULT_CSS}</style>{EXTRA_CSS}</head><body>{CONTENT}</body></html>',getDocType:function(){var e=d.config.doc.doctype,f=a.DOC_TYPE;if(e){f="<!DOCTYPE "+e.name+((e.publicId)?" "+e.publicId:"")+((e.systemId)?" "+e.systemId:"")+">";}else{if(d.config.doc.all){e=d.config.doc.all[0];if(e.nodeType){if(e.nodeType===8){if(e.nodeValue){if(e.nodeValue.toLowerCase().indexOf("doctype")!==-1){f="<!"+e.nodeValue+">";}}}}}}return f;},DOC_TYPE:'<!DOCTYPE HTML PUBLIC "-/'+"/W3C/"+"/DTD HTML 4.01/"+'/EN" "http:/'+'/www.w3.org/TR/html4/strict.dtd">',META:'<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/><meta http-equiv="X-UA-Compatible" content="IE=7">',NAME:"frame",ATTRS:{title:{value:"Blank Page"},dir:{value:"ltr"},lang:{value:"en-US"},src:{value:"javascript"+((d.UA.ie)?":false":":")+";"},designMode:{writeOnce:true,value:false},content:{value:"<br>",setter:"_setHTML",getter:"_getHTML"},basehref:{value:false,getter:"_resolveBaseHref"},use:{writeOnce:true,value:["substitute","node","node-style","selector-css3"]},container:{value:"body",setter:function(e){return d.one(e);}},node:{readOnly:true,value:null,getter:function(){return this._iframe;}},id:{writeOnce:true,getter:function(e){if(!e){e="iframe-"+d.guid();}return e;}},linkedcss:{value:"",getter:"_getLinkedCSS",setter:"_setLinkedCSS"},extracss:{value:"",setter:"_setExtraCSS"},host:{value:false},defaultblock:{value:"p"}}});d.Frame=a;},"3.4.0",{requires:["base","node","selector-css3","substitute","yui-throttle"],skinnable:false});