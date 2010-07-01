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

// DOM element creator for jQuery and Prototype by Michael Geary
// http://mg.to/topics/programming/javascript/jquery
// Inspired by MochiKit.DOM by Bob Ippolito
// Free beer and free speech. Enjoy!
$.defineTag = function( tag ) {
    $[tag.toUpperCase()] = function() {
        return $._createNode( tag, arguments );
    }
};

(function() {
    var tags = [
        'a', 'br', 'dd', 'dl',  'dt', 'button', 'canvas', 'div', 'fieldset', 'form',
        'h1', 'h2', 'h3', 'hr', 'img', 'input', 'label', 'legend',
        'li', 'link', 'ol', 'optgroup', 'option', 'p', 'pre', 'script', 'select',
        'span', 'strong', 'table', 'tbody', 'td', 'textarea',
        'tfoot', 'th', 'thead', 'tr', 'tt', 'ul' ];
    for( var i = tags.length - 1;  i >= 0;  i-- ) {
        $.defineTag( tags[i] );
    }
})();

$.NBSP = '\u00a0';

$._createNode = function( tag, args ) {
    var fix = { 'class':'className', 'Class':'className' };
    var e;
    try {
        var attrs = args[0] || {};
        e = document.createElement( tag );
        for( var attr in attrs ) {
            var a = fix[attr] || attr;
            // minimalist element style parser, no respect for ; or : even if quoted.
            if (a == "style") {
               var props = attrs[a].split(/;/)
               for (i=0; i < props.length; i++) {
                  var t = props[i].split(/:\s*/);
				  if(t.length != 2)continue;
                  var prop = t[0].replace(/^\s+|\s+$/g,'');
                  var val = t[1].replace(/^\s+|\s+$/g,'');
                  e.style[prop] = val;
               }
            } else if(a.indexOf('on') == 0){
				var eventConf = attrs[a];
				if(!(eventConf.handler instanceof Function))continue;
            	IS_Event.observe(e, a.substring(2), eventConf.handler, eventConf.capture, eventConf.id);
            } else {
				e[a] = attrs[attr];
			}
        }
        for( var i = 1;  i < args.length;  i++ ) {
            var arg = args[i];
            if( arg == null ) continue;
            if( arg.constructor != Array ) append( arg );
            else for( var j = 0;  j < arg.length;  j++ )
                append( arg[j] );
        }
    }
    catch( ex ) {
		console.log(ex);
        alert( 'Cannot create <' + tag + '> element:\n' +
            args.toSource() + '\n' + args );
        e = null;
    }

    function append( arg ) {
        if( arg == null ) return;
        var c = arg.constructor;
        switch( typeof arg ) {
            case 'number': arg = '' + arg;  // fall through
            case 'string': arg = document.createTextNode( arg );
        }
        e.appendChild( arg );
    }

    return e;
}; 
