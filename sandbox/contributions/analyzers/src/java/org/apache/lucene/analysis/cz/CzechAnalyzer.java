begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cz
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cz
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|LowerCaseFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
operator|.
name|WordlistLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Analyzer for Czech language. Supports an external list of stopwords (words that  * will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified, the  * exclusion list is empty by default.  *  * @author    Lukas Zapletal [lzap@root.cz]  */
end_comment

begin_class
DECL|class|CzechAnalyzer
specifier|public
specifier|final
class|class
name|CzechAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical stopwords. 	 */
DECL|field|STOP_WORDS
specifier|private
specifier|static
name|String
index|[]
name|STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"s"
block|,
literal|"k"
block|,
literal|"o"
block|,
literal|"i"
block|,
literal|"u"
block|,
literal|"v"
block|,
literal|"z"
block|,
literal|"dnes"
block|,
literal|"cz"
block|,
literal|"t\u00edmto"
block|,
literal|"bude\u0161"
block|,
literal|"budem"
block|,
literal|"byli"
block|,
literal|"jse\u0161"
block|,
literal|"m\u016fj"
block|,
literal|"sv\u00fdm"
block|,
literal|"ta"
block|,
literal|"tomto"
block|,
literal|"tohle"
block|,
literal|"tuto"
block|,
literal|"tyto"
block|,
literal|"jej"
block|,
literal|"zda"
block|,
literal|"pro\u010d"
block|,
literal|"m\u00e1te"
block|,
literal|"tato"
block|,
literal|"kam"
block|,
literal|"tohoto"
block|,
literal|"kdo"
block|,
literal|"kte\u0159\u00ed"
block|,
literal|"mi"
block|,
literal|"n\u00e1m"
block|,
literal|"tom"
block|,
literal|"tomuto"
block|,
literal|"m\u00edt"
block|,
literal|"nic"
block|,
literal|"proto"
block|,
literal|"kterou"
block|,
literal|"byla"
block|,
literal|"toho"
block|,
literal|"proto\u017ee"
block|,
literal|"asi"
block|,
literal|"ho"
block|,
literal|"na\u0161i"
block|,
literal|"napi\u0161te"
block|,
literal|"re"
block|,
literal|"co\u017e"
block|,
literal|"t\u00edm"
block|,
literal|"tak\u017ee"
block|,
literal|"sv\u00fdch"
block|,
literal|"jej\u00ed"
block|,
literal|"sv\u00fdmi"
block|,
literal|"jste"
block|,
literal|"aj"
block|,
literal|"tu"
block|,
literal|"tedy"
block|,
literal|"teto"
block|,
literal|"bylo"
block|,
literal|"kde"
block|,
literal|"ke"
block|,
literal|"prav\u00e9"
block|,
literal|"ji"
block|,
literal|"nad"
block|,
literal|"nejsou"
block|,
literal|"\u010di"
block|,
literal|"pod"
block|,
literal|"t\u00e9ma"
block|,
literal|"mezi"
block|,
literal|"p\u0159es"
block|,
literal|"ty"
block|,
literal|"pak"
block|,
literal|"v\u00e1m"
block|,
literal|"ani"
block|,
literal|"kdy\u017e"
block|,
literal|"v\u0161ak"
block|,
literal|"neg"
block|,
literal|"jsem"
block|,
literal|"tento"
block|,
literal|"\u010dl\u00e1nku"
block|,
literal|"\u010dl\u00e1nky"
block|,
literal|"aby"
block|,
literal|"jsme"
block|,
literal|"p\u0159ed"
block|,
literal|"pta"
block|,
literal|"jejich"
block|,
literal|"byl"
block|,
literal|"je\u0161t\u011b"
block|,
literal|"a\u017e"
block|,
literal|"bez"
block|,
literal|"tak\u00e9"
block|,
literal|"pouze"
block|,
literal|"prvn\u00ed"
block|,
literal|"va\u0161e"
block|,
literal|"kter\u00e1"
block|,
literal|"n\u00e1s"
block|,
literal|"nov\u00fd"
block|,
literal|"tipy"
block|,
literal|"pokud"
block|,
literal|"m\u016f\u017ee"
block|,
literal|"strana"
block|,
literal|"jeho"
block|,
literal|"sv\u00e9"
block|,
literal|"jin\u00e9"
block|,
literal|"zpr\u00e1vy"
block|,
literal|"nov\u00e9"
block|,
literal|"nen\u00ed"
block|,
literal|"v\u00e1s"
block|,
literal|"jen"
block|,
literal|"podle"
block|,
literal|"zde"
block|,
literal|"u\u017e"
block|,
literal|"b\u00fdt"
block|,
literal|"v\u00edce"
block|,
literal|"bude"
block|,
literal|"ji\u017e"
block|,
literal|"ne\u017e"
block|,
literal|"kter\u00fd"
block|,
literal|"by"
block|,
literal|"kter\u00e9"
block|,
literal|"co"
block|,
literal|"nebo"
block|,
literal|"ten"
block|,
literal|"tak"
block|,
literal|"m\u00e1"
block|,
literal|"p\u0159i"
block|,
literal|"od"
block|,
literal|"po"
block|,
literal|"jsou"
block|,
literal|"jak"
block|,
literal|"dal\u0161\u00ed"
block|,
literal|"ale"
block|,
literal|"si"
block|,
literal|"se"
block|,
literal|"ve"
block|,
literal|"to"
block|,
literal|"jako"
block|,
literal|"za"
block|,
literal|"zp\u011bt"
block|,
literal|"ze"
block|,
literal|"do"
block|,
literal|"pro"
block|,
literal|"je"
block|,
literal|"na"
block|,
literal|"atd"
block|,
literal|"atp"
block|,
literal|"jakmile"
block|,
literal|"p\u0159i\u010dem\u017e"
block|,
literal|"j\u00e1"
block|,
literal|"on"
block|,
literal|"ona"
block|,
literal|"ono"
block|,
literal|"oni"
block|,
literal|"ony"
block|,
literal|"my"
block|,
literal|"vy"
block|,
literal|"j\u00ed"
block|,
literal|"ji"
block|,
literal|"m\u011b"
block|,
literal|"mne"
block|,
literal|"jemu"
block|,
literal|"tomu"
block|,
literal|"t\u011bm"
block|,
literal|"t\u011bmu"
block|,
literal|"n\u011bmu"
block|,
literal|"n\u011bmu\u017e"
block|,
literal|"jeho\u017e"
block|,
literal|"j\u00ed\u017e"
block|,
literal|"jeliko\u017e"
block|,
literal|"je\u017e"
block|,
literal|"jako\u017e"
block|,
literal|"na\u010de\u017e"
block|,     }
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the StopFilter. 	 */
DECL|field|stoptable
specifier|private
name|HashSet
name|stoptable
decl_stmt|;
comment|/** 	 * Builds an analyzer. 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words.    *    * @deprecated 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|HashSet
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads stopwords hash from resource stream (file, database...).      * @param   wordfile    File containing the wordlist      * @param   encoding    Encoding used (win-1250, iso-8859-2, ...}, null for default system encoding      */
DECL|method|loadStopWords
specifier|public
name|void
name|loadStopWords
parameter_list|(
name|InputStream
name|wordfile
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
comment|// clear any previous table (if present)
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|InputStreamReader
name|isr
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|wordfile
argument_list|)
expr_stmt|;
else|else
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|wordfile
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|LineNumberReader
name|lnr
init|=
operator|new
name|LineNumberReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
name|String
name|word
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|lnr
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|stoptable
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|stoptable
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** 	 * Creates a TokenStream which tokenizes all the text in the provided Reader. 	 * 	 * @return  A TokenStream build from a StandardTokenizer filtered with 	 * 			StandardFilter, StopFilter, GermanStemFilter and LowerCaseFilter 	 */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

