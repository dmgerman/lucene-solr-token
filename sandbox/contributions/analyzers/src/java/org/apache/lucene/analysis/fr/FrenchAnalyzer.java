begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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

begin_comment
comment|/**  * Analyzer for french language. Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an other list is specified, the  * exclusionlist is empty by default.  *  * @author    Patrick Talbot (based on Gerhard Schwarz work for German)  * @version   $Id$  */
end_comment

begin_class
DECL|class|FrenchAnalyzer
specifier|public
specifier|final
class|class
name|FrenchAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * Extended list of typical french stopwords. 	 */
DECL|field|FRENCH_STOP_WORDS
specifier|private
name|String
index|[]
name|FRENCH_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"afin"
block|,
literal|"ai"
block|,
literal|"ainsi"
block|,
literal|"après"
block|,
literal|"attendu"
block|,
literal|"au"
block|,
literal|"aujourd"
block|,
literal|"auquel"
block|,
literal|"aussi"
block|,
literal|"autre"
block|,
literal|"autres"
block|,
literal|"aux"
block|,
literal|"auxquelles"
block|,
literal|"auxquels"
block|,
literal|"avait"
block|,
literal|"avant"
block|,
literal|"avec"
block|,
literal|"avoir"
block|,
literal|"c"
block|,
literal|"car"
block|,
literal|"ce"
block|,
literal|"ceci"
block|,
literal|"cela"
block|,
literal|"celle"
block|,
literal|"celles"
block|,
literal|"celui"
block|,
literal|"cependant"
block|,
literal|"certain"
block|,
literal|"certaine"
block|,
literal|"certaines"
block|,
literal|"certains"
block|,
literal|"ces"
block|,
literal|"cet"
block|,
literal|"cette"
block|,
literal|"ceux"
block|,
literal|"chez"
block|,
literal|"ci"
block|,
literal|"combien"
block|,
literal|"comme"
block|,
literal|"comment"
block|,
literal|"concernant"
block|,
literal|"contre"
block|,
literal|"d"
block|,
literal|"dans"
block|,
literal|"de"
block|,
literal|"debout"
block|,
literal|"dedans"
block|,
literal|"dehors"
block|,
literal|"delà"
block|,
literal|"depuis"
block|,
literal|"derrière"
block|,
literal|"des"
block|,
literal|"désormais"
block|,
literal|"desquelles"
block|,
literal|"desquels"
block|,
literal|"dessous"
block|,
literal|"dessus"
block|,
literal|"devant"
block|,
literal|"devers"
block|,
literal|"devra"
block|,
literal|"divers"
block|,
literal|"diverse"
block|,
literal|"diverses"
block|,
literal|"doit"
block|,
literal|"donc"
block|,
literal|"dont"
block|,
literal|"du"
block|,
literal|"duquel"
block|,
literal|"durant"
block|,
literal|"dès"
block|,
literal|"elle"
block|,
literal|"elles"
block|,
literal|"en"
block|,
literal|"entre"
block|,
literal|"environ"
block|,
literal|"est"
block|,
literal|"et"
block|,
literal|"etc"
block|,
literal|"etre"
block|,
literal|"eu"
block|,
literal|"eux"
block|,
literal|"excepté"
block|,
literal|"hormis"
block|,
literal|"hors"
block|,
literal|"hélas"
block|,
literal|"hui"
block|,
literal|"il"
block|,
literal|"ils"
block|,
literal|"j"
block|,
literal|"je"
block|,
literal|"jusqu"
block|,
literal|"jusque"
block|,
literal|"l"
block|,
literal|"la"
block|,
literal|"laquelle"
block|,
literal|"le"
block|,
literal|"lequel"
block|,
literal|"les"
block|,
literal|"lesquelles"
block|,
literal|"lesquels"
block|,
literal|"leur"
block|,
literal|"leurs"
block|,
literal|"lorsque"
block|,
literal|"lui"
block|,
literal|"là"
block|,
literal|"ma"
block|,
literal|"mais"
block|,
literal|"malgré"
block|,
literal|"me"
block|,
literal|"merci"
block|,
literal|"mes"
block|,
literal|"mien"
block|,
literal|"mienne"
block|,
literal|"miennes"
block|,
literal|"miens"
block|,
literal|"moi"
block|,
literal|"moins"
block|,
literal|"mon"
block|,
literal|"moyennant"
block|,
literal|"même"
block|,
literal|"mêmes"
block|,
literal|"n"
block|,
literal|"ne"
block|,
literal|"ni"
block|,
literal|"non"
block|,
literal|"nos"
block|,
literal|"notre"
block|,
literal|"nous"
block|,
literal|"néanmoins"
block|,
literal|"nôtre"
block|,
literal|"nôtres"
block|,
literal|"on"
block|,
literal|"ont"
block|,
literal|"ou"
block|,
literal|"outre"
block|,
literal|"où"
block|,
literal|"par"
block|,
literal|"parmi"
block|,
literal|"partant"
block|,
literal|"pas"
block|,
literal|"passé"
block|,
literal|"pendant"
block|,
literal|"plein"
block|,
literal|"plus"
block|,
literal|"plusieurs"
block|,
literal|"pour"
block|,
literal|"pourquoi"
block|,
literal|"proche"
block|,
literal|"près"
block|,
literal|"puisque"
block|,
literal|"qu"
block|,
literal|"quand"
block|,
literal|"que"
block|,
literal|"quel"
block|,
literal|"quelle"
block|,
literal|"quelles"
block|,
literal|"quels"
block|,
literal|"qui"
block|,
literal|"quoi"
block|,
literal|"quoique"
block|,
literal|"revoici"
block|,
literal|"revoilà"
block|,
literal|"s"
block|,
literal|"sa"
block|,
literal|"sans"
block|,
literal|"sauf"
block|,
literal|"se"
block|,
literal|"selon"
block|,
literal|"seront"
block|,
literal|"ses"
block|,
literal|"si"
block|,
literal|"sien"
block|,
literal|"sienne"
block|,
literal|"siennes"
block|,
literal|"siens"
block|,
literal|"sinon"
block|,
literal|"soi"
block|,
literal|"soit"
block|,
literal|"son"
block|,
literal|"sont"
block|,
literal|"sous"
block|,
literal|"suivant"
block|,
literal|"sur"
block|,
literal|"ta"
block|,
literal|"te"
block|,
literal|"tes"
block|,
literal|"tien"
block|,
literal|"tienne"
block|,
literal|"tiennes"
block|,
literal|"tiens"
block|,
literal|"toi"
block|,
literal|"ton"
block|,
literal|"tous"
block|,
literal|"tout"
block|,
literal|"toute"
block|,
literal|"toutes"
block|,
literal|"tu"
block|,
literal|"un"
block|,
literal|"une"
block|,
literal|"va"
block|,
literal|"vers"
block|,
literal|"voici"
block|,
literal|"voilà"
block|,
literal|"vos"
block|,
literal|"votre"
block|,
literal|"vous"
block|,
literal|"vu"
block|,
literal|"vôtre"
block|,
literal|"vôtres"
block|,
literal|"y"
block|,
literal|"à"
block|,
literal|"ça"
block|,
literal|"ès"
block|,
literal|"été"
block|,
literal|"être"
block|,
literal|"ô"
block|}
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the StopFilter. 	 */
DECL|field|stoptable
specifier|private
name|Hashtable
name|stoptable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Contains words that should be indexed but not stemmed. 	 */
DECL|field|excltable
specifier|private
name|Hashtable
name|excltable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Builds an analyzer. 	 */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|FRENCH_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
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
name|makeStopTable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from an array of Strings. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from a Hashtable. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Hashtable
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from the words contained in the given file. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a TokenStream which tokenizes all the text in the provided Reader. 	 * 	 * @return  A TokenStream build from a StandardTokenizer filtered with 	 * 			StandardFilter, StopFilter, FrenchStemFilter and LowerCaseFilter 	 */
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
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName must not be null"
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"readermust not be null"
argument_list|)
throw|;
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
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|FrenchStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

