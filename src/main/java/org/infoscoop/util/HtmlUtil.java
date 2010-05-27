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

/*
 * $Id: HtmlUtil.java,v 1.4 2009/02/17 08:44:08 hr-endoh Exp $
 *
 * Beacon-IT inicio Project
 * Copyright (c) 2003 by Beacon Information Technology, Inc.
 * 163-1507 Tokyo-to, Shinjuku-ku, Nishi-Shinjuku 1-6-1 Shinjuku L-Tower
 * All rights reserved.
 * ====================================================================
 */
package org.infoscoop.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

/**
 * A utility class about the HTML.
 * 
 * @author Atsuhiko Kimura
 */
public class HtmlUtil {
	private static Log log = LogFactory.getLog(HtmlUtil.class);
	
    //The dictionary of the entity of HTML4.0（the reference of a numerical value letter）
    private static final Map _numericRefDict = new HashMap();

    //The dictionary of the entity of HTML4.0（substance）
    private static final Map _entityDict = new HashMap();

    static {
        _numericRefDict.put("&nbsp;", "&#160;");
        _numericRefDict.put("&iexcl;", "&#161;");
        _numericRefDict.put("&cent;", "&#162;");
        _numericRefDict.put("&pound;", "&#163;");
        _numericRefDict.put("&curren;", "&#164;");
        _numericRefDict.put("&yen;", "&#165;");
        _numericRefDict.put("&brvbar;", "&#166;");
        _numericRefDict.put("&sect;", "&#167;");
        _numericRefDict.put("&uml;", "&#168;");
        _numericRefDict.put("&copy;", "&#169;");
        _numericRefDict.put("&ordf;", "&#170;");
        _numericRefDict.put("&laquo;", "&#171;");
        _numericRefDict.put("&not;", "&#172;");
        _numericRefDict.put("&shy;", "&#173;");
        _numericRefDict.put("&reg;", "&#174;");
        _numericRefDict.put("&macr;", "&#175;");
        _numericRefDict.put("&deg;", "&#176;");
        _numericRefDict.put("&plusmn;", "&#177;");
        _numericRefDict.put("&sup2;", "&#178;");
        _numericRefDict.put("&sup3;", "&#179;");
        _numericRefDict.put("&acute;", "&#180;");
        _numericRefDict.put("&micro;", "&#181;");
        _numericRefDict.put("&para;", "&#182;");
        _numericRefDict.put("&middot;", "&#183;");
        _numericRefDict.put("&cedil;", "&#184;");
        _numericRefDict.put("&sup1;", "&#185;");
        _numericRefDict.put("&ordm;", "&#186;");
        _numericRefDict.put("&raquo;", "&#187;");
        _numericRefDict.put("&frac14;", "&#188;");
        _numericRefDict.put("&frac12;", "&#189;");
        _numericRefDict.put("&frac34;", "&#190;");
        _numericRefDict.put("&iquest;", "&#191;");
        _numericRefDict.put("&Agrave;", "&#192;");
        _numericRefDict.put("&Aacute;", "&#193;");
        _numericRefDict.put("&Acirc;", "&#194;");
        _numericRefDict.put("&Atilde;", "&#195;");
        _numericRefDict.put("&Auml;", "&#196;");
        _numericRefDict.put("&Aring;", "&#197;");
        _numericRefDict.put("&AElig;", "&#198;");
        _numericRefDict.put("&Ccedil;", "&#199;");
        _numericRefDict.put("&Egrave;", "&#200;");
        _numericRefDict.put("&Eacute;", "&#201;");
        _numericRefDict.put("&Ecirc;", "&#202;");
        _numericRefDict.put("&Euml;", "&#203;");
        _numericRefDict.put("&Igrave;", "&#204;");
        _numericRefDict.put("&Iacute;", "&#205;");
        _numericRefDict.put("&Icirc;", "&#206;");
        _numericRefDict.put("&Iuml;", "&#207;");
        _numericRefDict.put("&ETH;", "&#208;");
        _numericRefDict.put("&Ntilde;", "&#209;");
        _numericRefDict.put("&Ograve;", "&#210;");
        _numericRefDict.put("&Oacute;", "&#211;");
        _numericRefDict.put("&Ocirc;", "&#212;");
        _numericRefDict.put("&Otilde;", "&#213;");
        _numericRefDict.put("&Ouml;", "&#214;");
        _numericRefDict.put("&times;", "&#215;");
        _numericRefDict.put("&Oslash;", "&#216;");
        _numericRefDict.put("&Ugrave;", "&#217;");
        _numericRefDict.put("&Uacute;", "&#218;");
        _numericRefDict.put("&Ucirc;", "&#219;");
        _numericRefDict.put("&Uuml;", "&#220;");
        _numericRefDict.put("&Yacute;", "&#221;");
        _numericRefDict.put("&THORN;", "&#222;");
        _numericRefDict.put("&szlig;", "&#223;");
        _numericRefDict.put("&agrave;", "&#224;");
        _numericRefDict.put("&aacute;", "&#225;");
        _numericRefDict.put("&acirc;", "&#226;");
        _numericRefDict.put("&atilde;", "&#227;");
        _numericRefDict.put("&auml;", "&#228;");
        _numericRefDict.put("&aring;", "&#229;");
        _numericRefDict.put("&aelig;", "&#230;");
        _numericRefDict.put("&ccedil;", "&#231;");
        _numericRefDict.put("&egrave;", "&#232;");
        _numericRefDict.put("&eacute;", "&#233;");
        _numericRefDict.put("&ecirc;", "&#234;");
        _numericRefDict.put("&euml;", "&#235;");
        _numericRefDict.put("&igrave;", "&#236;");
        _numericRefDict.put("&iacute;", "&#237;");
        _numericRefDict.put("&icirc;", "&#238;");
        _numericRefDict.put("&iuml;", "&#239;");
        _numericRefDict.put("&eth;", "&#240;");
        _numericRefDict.put("&ntilde;", "&#241;");
        _numericRefDict.put("&ograve;", "&#242;");
        _numericRefDict.put("&oacute;", "&#243;");
        _numericRefDict.put("&ocirc;", "&#244;");
        _numericRefDict.put("&otilde;", "&#245;");
        _numericRefDict.put("&ouml;", "&#246;");
        _numericRefDict.put("&divide;", "&#247;");
        _numericRefDict.put("&oslash;", "&#248;");
        _numericRefDict.put("&ugrave;", "&#249;");
        _numericRefDict.put("&uacute;", "&#250;");
        _numericRefDict.put("&ucirc;", "&#251;");
        _numericRefDict.put("&uuml;", "&#252;");
        _numericRefDict.put("&yacute;", "&#253;");
        _numericRefDict.put("&thorn;", "&#254;");
        _numericRefDict.put("&yuml;", "&#255;");
        _numericRefDict.put("&fnof;", "&#402;");
        _numericRefDict.put("&Alpha;", "&#913;");
        _numericRefDict.put("&Beta;", "&#914;");
        _numericRefDict.put("&Gamma;", "&#915;");
        _numericRefDict.put("&Delta;", "&#916;");
        _numericRefDict.put("&Epsilon;", "&#917;");
        _numericRefDict.put("&Zeta;", "&#918;");
        _numericRefDict.put("&Eta;", "&#919;");
        _numericRefDict.put("&Theta;", "&#920;");
        _numericRefDict.put("&Iota;", "&#921;");
        _numericRefDict.put("&Kappa;", "&#922;");
        _numericRefDict.put("&Lambda;", "&#923;");
        _numericRefDict.put("&Mu;", "&#924;");
        _numericRefDict.put("&Nu;", "&#925;");
        _numericRefDict.put("&Xi;", "&#926;");
        _numericRefDict.put("&Omicron;", "&#927;");
        _numericRefDict.put("&Pi;", "&#928;");
        _numericRefDict.put("&Rho;", "&#929;");
        _numericRefDict.put("&Sigma;", "&#931;");
        _numericRefDict.put("&Tau;", "&#932;");
        _numericRefDict.put("&Upsilon;", "&#933;");
        _numericRefDict.put("&Phi;", "&#934;");
        _numericRefDict.put("&Chi;", "&#935;");
        _numericRefDict.put("&Psi;", "&#936;");
        _numericRefDict.put("&Omega;", "&#937;");
        _numericRefDict.put("&alpha;", "&#945;");
        _numericRefDict.put("&beta;", "&#946;");
        _numericRefDict.put("&gamma;", "&#947;");
        _numericRefDict.put("&delta;", "&#948;");
        _numericRefDict.put("&epsilon;", "&#949;");
        _numericRefDict.put("&zeta;", "&#950;");
        _numericRefDict.put("&eta;", "&#951;");
        _numericRefDict.put("&theta;", "&#952;");
        _numericRefDict.put("&iota;", "&#953;");
        _numericRefDict.put("&kappa;", "&#954;");
        _numericRefDict.put("&lambda;", "&#955;");
        _numericRefDict.put("&mu;", "&#956;");
        _numericRefDict.put("&nu;", "&#957;");
        _numericRefDict.put("&xi;", "&#958;");
        _numericRefDict.put("&omicron;", "&#959;");
        _numericRefDict.put("&pi;", "&#960;");
        _numericRefDict.put("&rho;", "&#961;");
        _numericRefDict.put("&sigmaf;", "&#962;");
        _numericRefDict.put("&sigma;", "&#963;");
        _numericRefDict.put("&tau;", "&#964;");
        _numericRefDict.put("&upsilon;", "&#965;");
        _numericRefDict.put("&phi;", "&#966;");
        _numericRefDict.put("&chi;", "&#967;");
        _numericRefDict.put("&psi;", "&#968;");
        _numericRefDict.put("&omega;", "&#969;");
        _numericRefDict.put("&thetasym;", "&#977;");
        _numericRefDict.put("&upsih;", "&#978;");
        _numericRefDict.put("&piv;", "&#982;");
        _numericRefDict.put("&bull;", "&#8226;");
        _numericRefDict.put("&hellip;", "&#8230;");
        _numericRefDict.put("&prime;", "&#8242;");
        _numericRefDict.put("&Prime;", "&#8243;");
        _numericRefDict.put("&oline;", "&#8254;");
        _numericRefDict.put("&frasl;", "&#8260;");
        _numericRefDict.put("&weierp;", "&#8472;");
        _numericRefDict.put("&image;", "&#8465;");
        _numericRefDict.put("&real;", "&#8476;");
        _numericRefDict.put("&trade;", "&#8482;");
        _numericRefDict.put("&alefsym;", "&#8501;");
        _numericRefDict.put("&larr;", "&#8592;");
        _numericRefDict.put("&uarr;", "&#8593;");
        _numericRefDict.put("&rarr;", "&#8594;");
        _numericRefDict.put("&darr;", "&#8595;");
        _numericRefDict.put("&harr;", "&#8596;");
        _numericRefDict.put("&crarr;", "&#8629;");
        _numericRefDict.put("&lArr;", "&#8656;");
        _numericRefDict.put("&uArr;", "&#8657;");
        _numericRefDict.put("&rArr;", "&#8658;");
        _numericRefDict.put("&dArr;", "&#8659;");
        _numericRefDict.put("&hArr;", "&#8660;");
        _numericRefDict.put("&forall;", "&#8704;");
        _numericRefDict.put("&part;", "&#8706;");
        _numericRefDict.put("&exist;", "&#8707;");
        _numericRefDict.put("&empty;", "&#8709;");
        _numericRefDict.put("&nabla;", "&#8711;");
        _numericRefDict.put("&isin;", "&#8712;");
        _numericRefDict.put("&notin;", "&#8713;");
        _numericRefDict.put("&ni;", "&#8715;");
        _numericRefDict.put("&prod;", "&#8719;");
        _numericRefDict.put("&sum;", "&#8721;");
        _numericRefDict.put("&minus;", "&#8722;");
        _numericRefDict.put("&lowast;", "&#8727;");
        _numericRefDict.put("&radic;", "&#8730;");
        _numericRefDict.put("&prop;", "&#8733;");
        _numericRefDict.put("&infin;", "&#8734;");
        _numericRefDict.put("&ang;", "&#8736;");
        _numericRefDict.put("&and;", "&#8743;");
        _numericRefDict.put("&or;", "&#8744;");
        _numericRefDict.put("&cap;", "&#8745;");
        _numericRefDict.put("&cup;", "&#8746;");
        _numericRefDict.put("&int;", "&#8747;");
        _numericRefDict.put("&there4;", "&#8756;");
        _numericRefDict.put("&sim;", "&#8764;");
        _numericRefDict.put("&cong;", "&#8773;");
        _numericRefDict.put("&asymp;", "&#8776;");
        _numericRefDict.put("&ne;", "&#8800;");
        _numericRefDict.put("&equiv;", "&#8801;");
        _numericRefDict.put("&le;", "&#8804;");
        _numericRefDict.put("&ge;", "&#8805;");
        _numericRefDict.put("&sub;", "&#8834;");
        _numericRefDict.put("&sup;", "&#8835;");
        _numericRefDict.put("&nsub;", "&#8836;");
        _numericRefDict.put("&sube;", "&#8838;");
        _numericRefDict.put("&supe;", "&#8839;");
        _numericRefDict.put("&oplus;", "&#8853;");
        _numericRefDict.put("&otimes;", "&#8855;");
        _numericRefDict.put("&perp;", "&#8869;");
        _numericRefDict.put("&sdot;", "&#8901;");
        _numericRefDict.put("&lceil;", "&#8968;");
        _numericRefDict.put("&rceil;", "&#8969;");
        _numericRefDict.put("&lfloor;", "&#8970;");
        _numericRefDict.put("&rfloor;", "&#8971;");
        _numericRefDict.put("&lang;", "&#9001;");
        _numericRefDict.put("&rang;", "&#9002;");
        _numericRefDict.put("&loz;", "&#9674;");
        _numericRefDict.put("&spades;", "&#9824;");
        _numericRefDict.put("&clubs;", "&#9827;");
        _numericRefDict.put("&hearts;", "&#9829;");
        _numericRefDict.put("&diams;", "&#9830;");
        _numericRefDict.put("&quot;", "&#34;");
        _numericRefDict.put("&amp;", "&#38;");
        _numericRefDict.put("&lt;", "&#60;");
        _numericRefDict.put("&gt;", "&#62;");
        _numericRefDict.put("&OElig;", "&#338;");
        _numericRefDict.put("&oelig;", "&#339;");
        _numericRefDict.put("&Scaron;", "&#352;");
        _numericRefDict.put("&scaron;", "&#353;");
        _numericRefDict.put("&Yuml;", "&#376;");
        _numericRefDict.put("&circ;", "&#710;");
        _numericRefDict.put("&tilde;", "&#732;");
        _numericRefDict.put("&ensp;", "&#8194;");
        _numericRefDict.put("&emsp;", "&#8195;");
        _numericRefDict.put("&thinsp;", "&#8201;");
        _numericRefDict.put("&zwnj;", "&#8204;");
        _numericRefDict.put("&zwj;", "&#8205;");
        _numericRefDict.put("&lrm;", "&#8206;");
        _numericRefDict.put("&rlm;", "&#8207;");
        _numericRefDict.put("&ndash;", "&#8211;");
        _numericRefDict.put("&mdash;", "&#8212;");
        _numericRefDict.put("&lsquo;", "&#8216;");
        _numericRefDict.put("&rsquo;", "&#8217;");
        _numericRefDict.put("&sbquo;", "&#8218;");
        _numericRefDict.put("&ldquo;", "&#8220;");
        _numericRefDict.put("&rdquo;", "&#8221;");
        _numericRefDict.put("&bdquo;", "&#8222;");
        _numericRefDict.put("&dagger;", "&#8224;");
        _numericRefDict.put("&Dagger;", "&#8225;");
        _numericRefDict.put("&permil;", "&#8240;");
        _numericRefDict.put("&lsaquo;", "&#8249;");
        _numericRefDict.put("&rsaquo;", "&#8250;");
        _numericRefDict.put("&euro;", "&#8364;");
    }

    static {
        _entityDict.put("&nbsp;", "\u00a0");
        _entityDict.put("&iexcl;", "\u00a1");
        _entityDict.put("&cent;", "\u00a2");
        _entityDict.put("&pound;", "\u00a3");
        _entityDict.put("&curren;", "\u00a4");
        _entityDict.put("&yen;", "\u00a5");
        _entityDict.put("&brvbar;", "\u00a6");
        _entityDict.put("&sect;", "\u00a7");
        _entityDict.put("&uml;", "\u00a8");
        _entityDict.put("&copy;", "\u00a9");
        _entityDict.put("&ordf;", "\u00aa");
        _entityDict.put("&laquo;", "\u00ab");
        _entityDict.put("&not;", "\u00ac");
        _entityDict.put("&shy;", "\u00ad");
        _entityDict.put("&reg;", "\u00ae");
        _entityDict.put("&macr;", "\u00af");
        _entityDict.put("&deg;", "\u00b0");
        _entityDict.put("&plusmn;", "\u00b1");
        _entityDict.put("&sup2;", "\u00b2");
        _entityDict.put("&sup3;", "\u00b3");
        _entityDict.put("&acute;", "\u00b4");
        _entityDict.put("&micro;", "\u00b5");
        _entityDict.put("&para;", "\u00b6");
        _entityDict.put("&middot;", "\u00b7");
        _entityDict.put("&cedil;", "\u00b8");
        _entityDict.put("&sup1;", "\u00b9");
        _entityDict.put("&ordm;", "\u00ba");
        _entityDict.put("&raquo;", "\u00bb");
        _entityDict.put("&frac14;", "\u00bc");
        _entityDict.put("&frac12;", "\u00bd");
        _entityDict.put("&frac34;", "\u00be");
        _entityDict.put("&iquest;", "\u00bf");
        _entityDict.put("&Agrave;", "\u00c0");
        _entityDict.put("&Aacute;", "\u00c1");
        _entityDict.put("&Acirc;", "\u00c2");
        _entityDict.put("&Atilde;", "\u00c3");
        _entityDict.put("&Auml;", "\u00c4");
        _entityDict.put("&Aring;", "\u00c5");
        _entityDict.put("&AElig;", "\u00c6");
        _entityDict.put("&Ccedil;", "\u00c7");
        _entityDict.put("&Egrave;", "\u00c8");
        _entityDict.put("&Eacute;", "\u00c9");
        _entityDict.put("&Ecirc;", "\u00ca");
        _entityDict.put("&Euml;", "\u00cb");
        _entityDict.put("&Igrave;", "\u00cc");
        _entityDict.put("&Iacute;", "\u00cd");
        _entityDict.put("&Icirc;", "\u00ce");
        _entityDict.put("&Iuml;", "\u00cf");
        _entityDict.put("&ETH;", "\u00d0");
        _entityDict.put("&Ntilde;", "\u00d1");
        _entityDict.put("&Ograve;", "\u00d2");
        _entityDict.put("&Oacute;", "\u00d3");
        _entityDict.put("&Ocirc;", "\u00d4");
        _entityDict.put("&Otilde;", "\u00d5");
        _entityDict.put("&Ouml;", "\u00d6");
        _entityDict.put("&times;", "\u00d7");
        _entityDict.put("&Oslash;", "\u00d8");
        _entityDict.put("&Ugrave;", "\u00d9");
        _entityDict.put("&Uacute;", "\u00da");
        _entityDict.put("&Ucirc;", "\u00db");
        _entityDict.put("&Uuml;", "\u00dc");
        _entityDict.put("&Yacute;", "\u00dd");
        _entityDict.put("&THORN;", "\u00de");
        _entityDict.put("&szlig;", "\u00df");
        _entityDict.put("&agrave;", "\u00e0");
        _entityDict.put("&aacute;", "\u00e1");
        _entityDict.put("&acirc;", "\u00e2");
        _entityDict.put("&atilde;", "\u00e3");
        _entityDict.put("&auml;", "\u00e4");
        _entityDict.put("&aring;", "\u00e5");
        _entityDict.put("&aelig;", "\u00e6");
        _entityDict.put("&ccedil;", "\u00e7");
        _entityDict.put("&egrave;", "\u00e8");
        _entityDict.put("&eacute;", "\u00e9");
        _entityDict.put("&ecirc;", "\u00ea");
        _entityDict.put("&euml;", "\u00eb");
        _entityDict.put("&igrave;", "\u00ec");
        _entityDict.put("&iacute;", "\u00ed");
        _entityDict.put("&icirc;", "\u00ee");
        _entityDict.put("&iuml;", "\u00ef");
        _entityDict.put("&eth;", "\u00f0");
        _entityDict.put("&ntilde;", "\u00f1");
        _entityDict.put("&ograve;", "\u00f2");
        _entityDict.put("&oacute;", "\u00f3");
        _entityDict.put("&ocirc;", "\u00f4");
        _entityDict.put("&otilde;", "\u00f5");
        _entityDict.put("&ouml;", "\u00f6");
        _entityDict.put("&divide;", "\u00f7");
        _entityDict.put("&oslash;", "\u00f8");
        _entityDict.put("&ugrave;", "\u00f9");
        _entityDict.put("&uacute;", "\u00fa");
        _entityDict.put("&ucirc;", "\u00fb");
        _entityDict.put("&uuml;", "\u00fc");
        _entityDict.put("&yacute;", "\u00fd");
        _entityDict.put("&thorn;", "\u00fe");
        _entityDict.put("&yuml;", "\u00ff");
        _entityDict.put("&fnof;", "\u0192");
        _entityDict.put("&Alpha;", "\u0391");
        _entityDict.put("&Beta;", "\u0392");
        _entityDict.put("&Gamma;", "\u0393");
        _entityDict.put("&Delta;", "\u0394");
        _entityDict.put("&Epsilon;", "\u0395");
        _entityDict.put("&Zeta;", "\u0396");
        _entityDict.put("&Eta;", "\u0397");
        _entityDict.put("&Theta;", "\u0398");
        _entityDict.put("&Iota;", "\u0399");
        _entityDict.put("&Kappa;", "\u039a");
        _entityDict.put("&Lambda;", "\u039b");
        _entityDict.put("&Mu;", "\u039c");
        _entityDict.put("&Nu;", "\u039d");
        _entityDict.put("&Xi;", "\u039e");
        _entityDict.put("&Omicron;", "\u039f");
        _entityDict.put("&Pi;", "\u03a0");
        _entityDict.put("&Rho;", "\u03a1");
        _entityDict.put("&Sigma;", "\u03a3");
        _entityDict.put("&Tau;", "\u03a4");
        _entityDict.put("&Upsilon;", "\u03a5");
        _entityDict.put("&Phi;", "\u03a6");
        _entityDict.put("&Chi;", "\u03a7");
        _entityDict.put("&Psi;", "\u03a8");
        _entityDict.put("&Omega;", "\u03a9");
        _entityDict.put("&alpha;", "\u03b1");
        _entityDict.put("&beta;", "\u03b2");
        _entityDict.put("&gamma;", "\u03b3");
        _entityDict.put("&delta;", "\u03b4");
        _entityDict.put("&epsilon;", "\u03b5");
        _entityDict.put("&zeta;", "\u03b6");
        _entityDict.put("&eta;", "\u03b7");
        _entityDict.put("&theta;", "\u03b8");
        _entityDict.put("&iota;", "\u03b9");
        _entityDict.put("&kappa;", "\u03ba");
        _entityDict.put("&lambda;", "\u03bb");
        _entityDict.put("&mu;", "\u03bc");
        _entityDict.put("&nu;", "\u03bd");
        _entityDict.put("&xi;", "\u03be");
        _entityDict.put("&omicron;", "\u03bf");
        _entityDict.put("&pi;", "\u03c0");
        _entityDict.put("&rho;", "\u03c1");
        _entityDict.put("&sigmaf;", "\u03c2");
        _entityDict.put("&sigma;", "\u03c3");
        _entityDict.put("&tau;", "\u03c4");
        _entityDict.put("&upsilon;", "\u03c5");
        _entityDict.put("&phi;", "\u03c6");
        _entityDict.put("&chi;", "\u03c7");
        _entityDict.put("&psi;", "\u03c8");
        _entityDict.put("&omega;", "\u03c9");
        _entityDict.put("&thetasym;", "\u03d1");
        _entityDict.put("&upsih;", "\u03d2");
        _entityDict.put("&piv;", "\u03d6");
        _entityDict.put("&bull;", "\u2022");
        _entityDict.put("&hellip;", "\u2026");
        _entityDict.put("&prime;", "\u2032");
        _entityDict.put("&Prime;", "\u2033");
        _entityDict.put("&oline;", "\u203e");
        _entityDict.put("&frasl;", "\u2044");
        _entityDict.put("&weierp;", "\u2118");
        _entityDict.put("&image;", "\u2111");
        _entityDict.put("&real;", "\u211c");
        _entityDict.put("&trade;", "\u2122");
        _entityDict.put("&alefsym;", "\u2135");
        _entityDict.put("&larr;", "\u2190");
        _entityDict.put("&uarr;", "\u2191");
        _entityDict.put("&rarr;", "\u2192");
        _entityDict.put("&darr;", "\u2193");
        _entityDict.put("&harr;", "\u2194");
        _entityDict.put("&crarr;", "\u21b5");
        _entityDict.put("&lArr;", "\u21d0");
        _entityDict.put("&uArr;", "\u21d1");
        _entityDict.put("&rArr;", "\u21d2");
        _entityDict.put("&dArr;", "\u21d3");
        _entityDict.put("&hArr;", "\u21d4");
        _entityDict.put("&forall;", "\u2200");
        _entityDict.put("&part;", "\u2202");
        _entityDict.put("&exist;", "\u2203");
        _entityDict.put("&empty;", "\u2205");
        _entityDict.put("&nabla;", "\u2207");
        _entityDict.put("&isin;", "\u2208");
        _entityDict.put("&notin;", "\u2209");
        _entityDict.put("&ni;", "\u220b");
        _entityDict.put("&prod;", "\u220f");
        _entityDict.put("&sum;", "\u2211");
        _entityDict.put("&minus;", "\u2212");
        _entityDict.put("&lowast;", "\u2217");
        _entityDict.put("&radic;", "\u221a");
        _entityDict.put("&prop;", "\u221d");
        _entityDict.put("&infin;", "\u221e");
        _entityDict.put("&ang;", "\u2220");
        _entityDict.put("&and;", "\u2227");
        _entityDict.put("&or;", "\u2228");
        _entityDict.put("&cap;", "\u2229");
        _entityDict.put("&cup;", "\u222a");
        _entityDict.put("&int;", "\u222b");
        _entityDict.put("&there4;", "\u2234");
        _entityDict.put("&sim;", "\u223c");
        _entityDict.put("&cong;", "\u2245");
        _entityDict.put("&asymp;", "\u2248");
        _entityDict.put("&ne;", "\u2260");
        _entityDict.put("&equiv;", "\u2261");
        _entityDict.put("&le;", "\u2264");
        _entityDict.put("&ge;", "\u2265");
        _entityDict.put("&sub;", "\u2282");
        _entityDict.put("&sup;", "\u2283");
        _entityDict.put("&nsub;", "\u2284");
        _entityDict.put("&sube;", "\u2286");
        _entityDict.put("&supe;", "\u2287");
        _entityDict.put("&oplus;", "\u2295");
        _entityDict.put("&otimes;", "\u2297");
        _entityDict.put("&perp;", "\u22a5");
        _entityDict.put("&sdot;", "\u22c5");
        _entityDict.put("&lceil;", "\u2308");
        _entityDict.put("&rceil;", "\u2309");
        _entityDict.put("&lfloor;", "\u230a");
        _entityDict.put("&rfloor;", "\u230b");
        _entityDict.put("&lang;", "\u2329");
        _entityDict.put("&rang;", "\u232a");
        _entityDict.put("&loz;", "\u25ca");
        _entityDict.put("&spades;", "\u2660");
        _entityDict.put("&clubs;", "\u2663");
        _entityDict.put("&hearts;", "\u2665");
        _entityDict.put("&diams;", "\u2666");
        _entityDict.put("&quot;", "\"");
        _entityDict.put("&amp;", "\u0026");
        _entityDict.put("&lt;", "\u003c");
        _entityDict.put("&gt;", "\u003e");
        _entityDict.put("&OElig;", "\u0152");
        _entityDict.put("&oelig;", "\u0153");
        _entityDict.put("&Scaron;", "\u0160");
        _entityDict.put("&scaron;", "\u0161");
        _entityDict.put("&Yuml;", "\u0178");
        _entityDict.put("&circ;", "\u02c6");
        _entityDict.put("&tilde;", "\u02dc");
        _entityDict.put("&ensp;", "\u2002");
        _entityDict.put("&emsp;", "\u2003");
        _entityDict.put("&thinsp;", "\u2009");
        _entityDict.put("&zwnj;", "\u200c");
        _entityDict.put("&zwj;", "\u200d");
        _entityDict.put("&lrm;", "\u200e");
        _entityDict.put("&rlm;", "\u200f");
        _entityDict.put("&ndash;", "\u2013");
        _entityDict.put("&mdash;", "\u2014");
        _entityDict.put("&lsquo;", "\u2018");
        _entityDict.put("&rsquo;", "\u2019");
        _entityDict.put("&sbquo;", "\u201a");
        _entityDict.put("&ldquo;", "\u201c");
        _entityDict.put("&rdquo;", "\u201d");
        _entityDict.put("&bdquo;", "\u201e");
        _entityDict.put("&dagger;", "\u2020");
        _entityDict.put("&Dagger;", "\u2021");
        _entityDict.put("&permil;", "\u2030");
        _entityDict.put("&lsaquo;", "\u2039");
        _entityDict.put("&rsaquo;", "\u203a");
        _entityDict.put("&euro;", "\u20ac");
    }

    /**
     * substitute "&lt;&gt;&quot;&amp;" for "&amp;lt;&amp;gt;&amp;quot;&amp;amp;".
     * 
     * @param str
     * @return
     */
    public static String escapeHtmlEntities(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            result.append(escapeHtmlEntities(str.charAt(i)));
        }
        return result.toString();
    }

    public static String escapeHtmlURL(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '&') {
                result.append("&amp;");
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * substitute "&lt;&gt;&quot;&amp;" for "&amp;lt;&amp;gt;&amp;quot;&amp;amp;".
     * 
     * @param ch
     * @return
     */
    public static String escapeHtmlEntities(char ch) {
        if (ch == '&') {
            return "&amp;";
        } else if (ch == '<') {
            return "&lt;";
        } else if (ch == '>') {
            return "&gt;";
        } else if (ch == '"') {
            return "&quot;";
        } else {
            return Character.toString(ch);
        }
    }
    /**
     *  encode the value of the attribute.
     * 
     * @param str
     * @return
     */
    public static String escapeAttributeValue(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
        	char ch = str.charAt(i);
        	switch(ch) {
        	case '\"':
        		result.append("&quot;");
        		break;
        	case '\'':
        	case '\\':
        		result.append("\\");
        		// fall through
        	default:
        		result.append(ch);
        	}
        }
        return result.toString();
    }

    /**
     * analyze an entity defined in HTML4.0.
     * 
     * @param str
     * @return
     */
    public static String resolveHtmlEntities(String str) {
        return StringUtil.replaceMap(str, _entityDict);
    }

    /**
     * convert a entity into a reference of a numerical value letter.
     * 
     * @param str
     * @return
     */
    public static String translateNumEntities(String str) {
        return StringUtil.replaceMap(str, _numericRefDict);
    }

    /**
     * @param id
     * @return
     */
    public static String encodeUrl(String id) {
        try {
            return URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            
            return id;
        }
    }

    /**
     * convert HTML into XML.
     * 
     * @param str
     * @return
     */
    public static String html2Xml(String str) {
        StringWriter result = new StringWriter();
        XMLParserConfiguration parser = new HTMLConfiguration();
        XMLDocumentFilter[] filters = { new XHtmlWriter(result) };
        parser
                .setFeature(
                        "http://cyberneko.org/html/features/balance-tags/document-fragment",
                        true);
        parser.setProperty("http://cyberneko.org/html/properties/filters",
                filters);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems",
                "lower");
        parser.setProperty("http://cyberneko.org/html/properties/names/attrs",
                "lower");
        try {
            parser.parse(new XMLInputSource(null, null, null, new StringReader(
                    str), null));
        } catch (IOException e) {
        	log.error("", e);
            return "Error in parse html.";
        }
        return result.toString();
    }

    /**
     * convert HTML into XML.
     * 
     * @param str
     * @return
     */
    public static String html2text(String str) {
		StringWriter result = new StringWriter();
		XMLParserConfiguration parser = new HTMLConfiguration();
		XMLDocumentFilter[] filters = { new HtmlTextWriter(result) };
		parser
				.setFeature(
						"http://cyberneko.org/html/features/balance-tags/document-fragment",
						true);
		parser.setProperty("http://cyberneko.org/html/properties/filters",
				filters);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems",
				"lower");
		parser.setProperty("http://cyberneko.org/html/properties/names/attrs",
				"lower");
		try {
			parser.parse(new XMLInputSource(null, null, null, new StringReader(
					str), null));
        } catch (IOException e) {
        	log.error("", e);
            return "Error in parse html.";
        }
        return result.toString();
    }

    /**
     *  decode the text of the appointed tag. 
     * 
     * @param html
     * @return
     */
    public static String decode(String html, String tagName) {
        String str = html;
        int start = 0;
        while (start >= 0) {
            start = str.indexOf("<" + tagName, start);
            if (start >= 0) {
                start = str.indexOf(">", start);
                if (start > 0) {
                    int end = str.indexOf("</" + tagName + ">", start);
                    if (end > start) {
                        String script = str.substring(start + 1, end);
                        script = XmlUtil.resolveNumEntities(script);
                        str = str.substring(0, start + 1) + script
                                + str.substring(end);
                        start = end;
                    }
                }
            }
        }
        return str;
    }
}
