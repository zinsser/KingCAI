(function(){var s=window,b=s.bShareUtil,a=s.bShare,e=a.config,f=a.iL8n,c=e.popHCol,t=e.poptxtc,z=e.popbgc,E=a.bhost,A=a.whost,u=a.imageBasePath,F=[],G=["bsharesync","xinhuamb"],B="sinaminiblog,qqmb,renren,neteasemb,sohuminiblog,kaixin001,qzone,tianya,bsharesync".split(","),j=document,m=j.documentElement,n=j.body,o=Math.max,k=0,v=0,w=0,C=e.lang==="en",H=function(){var l=j.getElementById("bsPanelHolder");if(!l)l=j.createElement("div"),l.id="bsPanelHolder";var k=l;b.loadStyle("a.bsSiteLink{text-decoration:none;color:"+
t+";}a.bshareDiv{overflow:hidden;height:16px;line-height:18px;font-size:14px;color:#333;padding-left:0;}a.bshareDiv:hover{text-decoration:none;}div.bsTitle{padding:0 8px;border-bottom:1px solid #e8e8e8;color:"+t+";background:"+z+";text-align:left;}a.bsSiteLink:hover{text-decoration:underline;}div.buzzButton{cursor:pointer;}div.bsRlogo,div.bsRlogoSel{width:68px;float:left;margin:0;padding:2px 0;}div.bsRlogo a,div.bsRlogoSel a{float:left;}div.bsLogo,div.bsLogoSel{float:left;width:111px;text-align:left;height:auto;padding:2px 4px;margin:2px 0;white-space:nowrap;overflow:hidden;}div.bsLogoSel,div.bsRlogoSel{border:1px solid #ddd;background:#f1f1f1;}div.bsLogo,div.bsRlogo{border:1px solid #fff;background:#fff;}div.bsLogo a,div.bsLogoSel a{display:block;height:16px;line-height:16px;padding:0 0 0 24px;text-decoration:none;float:left;overflow:hidden;}div.bsLogoSel a,div.bsRlogoSel a{color:#000;border:none;}div.bsLogo a,div.bsRlogo a{color:#666;border:none;}div.bsLogoLink{width:121px;overflow:hidden;background:#FFF;float:left;margin:3px 0;}#bsLogin{float:right;text-align:right;overflow:hidden;height:100%;}#bsPanel{position:absolute;z-index:100000000;font-size:12px;width:"+
(111*c+(c-1)*10+26+(b.isIe&&!b.isSt?6:0))+"px;background:url("+a.shost+"/frame/images/background-opaque-dark."+(b.isIe6?"gif":"png")+");padding:6px;-moz-border-radius:5px;-webkit-border-radius:5px;border-radius:5px;}div.bsClear{clear:both;height:0;line-height:0;font-size:0;overflow:hidden;}div.bsPopupAwd{background: url("+u+"/bshare_box_sprite2.gif) no-repeat top left;background-position:0 -624px;width:18px;padding-left:3px;text-align:center;float:left;margin-left: 2px;height:15px;font-size:12px;color:#fff;overflow:hidden;}div.bsRlogo .bsPopupAwd,div.bsRlogoSel .bsPopupAwd{float:left;margin:5px 0px 0px -14px;}");
var q;q='<div class="bsTitle"><a style="float:left;height:20px;line-height:20px;font-weight:bold;" class="bsSiteLink" target="_blank" href="'+A+'/intro">'+f.shareText+'</a><a class="bsSiteLink" style="cursor:pointer;float:right;height:20px;line-height:20px;font-weight:bold;" onclick="document.getElementById(\'bsPanel\').style.display=\'none\';">X</a><div class="bsClear"></div></div><div class="bsClear"></div>';var i="",g,d,x,h=[],r,m,p,n,y,o=0,D;if(c==2){i+='<div style="height:47px;border-bottom:1px #ccc solid;padding:4px 0 4px 16px;margin-right:8px;_padding-left:12px;">';
for(g=0;g<3&&g<e.bps.length;g++)d=e.bps[g],a.pnMap[d]!==void 0&&(p=!!a.params.promote&&!C&&b.hasElem(B,d),i+='<div class="bsRlogo" onmouseover="javascript:this.className=\'bsRlogoSel\'" onmouseout="javascript:this.className=\'bsRlogo\'"><a href="javascript:void(0);" onclick="javascript:bShare.share(event,\''+d+'\');return false;" style="text-decoration:none;line-height:120%;"><div style="cursor:pointer;width:24px;height:24px;margin:0 18px 2px;background:url('+u+"/logos/m2/"+d+'.gif) no-repeat;"></div><div style="cursor:pointer;text-align:center;width:60px;height:16px !important;overflow:hidden;color:inherit;white-space:nowrap;line-height:120% !important">'+
a.pnMap[d][0]+"</div></a>"+(p?'<div class="bsPopupAwd">'+f.promoteShort+"</div>":"")+"</div>");i+="</div>"}for(d=0;d<c;d++)h.push("<div class='bsLogoLink'>");m=c<2&&e.bps.length>6?6:e.bps.length;for(g=0,r=c==2?3:0;r<m;r++)d=e.bps[r],a.pnMap[d]!==void 0&&(x=a.pnMap[d][0],y=a.pnMap[d][1]*-18,p=!!a.params.promote&&!C&&b.hasElem(B,d),n=(b.hasElem(F,d)?"font-weight: bold;":"")+(b.hasElem(G,d)?"color: red;":"")+(p?"width: 48px;":""),h[g%c]+='<div class="bsLogo" onmouseover="javascript:this.className=\'bsLogoSel\'" onmouseout="javascript:this.className=\'bsLogo\'"><a href="javascript:void(0);" title="'+
x+'" onclick="javascript:bShare.share(event,\''+d+'\');return false;" style="'+n+"background:url("+u+(y?"/slogos_sprite8."+(b.isIe?"gif":"png")+") no-repeat 0 "+y+'px;">':"/logos/s4/"+d+(b.isIe?".gif":".png")+') no-repeat;">')+x+"</a>"+(p?'<div class="bsPopupAwd">'+f.promoteShort+"</div>":"")+"</div>",g++);v=116+26*Math.ceil(g/c)-(c!=2?56:0);w=c*121+28;for(d=0;d<c;d++)i+=h[d]+"</div>";i+="<div class='bsClear'></div>";for(D in a.pnMap)a.pnMap.hasOwnProperty(D)&&o++;h='<div style="height:20px;line-height:20px;padding:0 8px;border-top:1px solid #e8e8e8;color:'+
t+";background:"+z+';"><div class="buzzButton" style="float:left;">'+(c==1?f.morePlatsShort:f.morePlats+' <font style="font-weight:normal;">('+o+")</font>")+'</div><div id="bsLogin" style="'+(c<2?"width:80px;":"")+'">';e.logo&&(h+='<a class="bsSiteLink" href="'+A,h+=a.isLite?'" target="_blank"><span style="font-size:10px;vertical-align:bottom;line-height:24px;"><span style="color:#f60;">b</span>Share</span></a>':a.user?'/userProfile" title="'+a.user+'" target="_blank" style="width:120px;display:block;overflow:hidden;">'+
a.user+"</a>":'/register" title="'+f.regTitle+'" target="_blank">'+f.register+'</a>&nbsp;&nbsp;<a class="bsSiteLink" title="'+f.loginTitle+'" target="_blank" href="'+E+"/loginAction?originalUrl="+s.location+'" onclick="bShare.more();">'+f.login+"</a>");h+="</div></div>";q=q+"<div style='padding-left:8px;background:#fff;*height:"+(26*Math.ceil(g/c)+6+(c==2?56:0))+"px;'>"+i+"</div>"+h;k.innerHTML='<div id="bsPanel" style="display:none;">'+q+"</div>";j.body.appendChild(l);return l};a.ready=function(){H();
var c=j.getElementById("bsPanel");a.hover=function(c){k!==0&&clearTimeout(k);var i=c||b.getElem(j,"a","buzzButton")[0],g=b.getOffset(i).y,d=b.getOffset(i).x,f=i.offsetHeight,h=j.getElementById("bsPanel"),l=b.getWH().w;b.getWH();if(e.poph!=="right"&&(d+w>l||e.poph==="left"))d-=w-i.offsetWidth;h.style.left=d+"px";h.style.top=e.pop==2||e.pop!=1&&g-{t:o(m.scrollTop,n.scrollTop),l:o(m.scrollLeft,n.scrollLeft)}.t+v+f>b.getWH().h?g-v-2+"px":g+f+2+"px";h.style.display="";a.prepare(c.index);a.click()};var f=
function(b){e.pop===-2?b.onclick=function(){a.hover(this)}:(b.onmouseover=function(){a.hover(this)},b.onmouseout=function(){k=setTimeout(function(){c.style.display="none"},20)})};a.style!=5&&b.getElem(j,"a","bshareDiv",function(a){b.getElem(a,"div","buzzButton",function(a,b){a.index=b;f(a)})});b.getElem(j,"div","bshare-custom",function(a){b.getElem(a,"a","bshare-more",function(a,b){a.index=b;f(a)})});b.getElem(c,"div","buzzButton",function(b,c){b.onclick=function(b){a.more(b,c);return!1}});c.onmouseout=
function(){k=setTimeout(function(){c.style.display="none"},0)};c.onmouseover=function(){k!==0&&clearTimeout(k)}};!e.bps||e.bps.length==0?setTimeout(function(){if(!e.bps||e.bps.length==0)e.bps="bsharesync,sinaminiblog,qqmb,renren,qzone,sohuminiblog,douban,kaixin001,baiduhi,qqxiaoyou,neteasemb,ifengmb,email,facebook,twitter,tianya,clipboard".split(","),a.ready()},2E3):a.ready()})();
