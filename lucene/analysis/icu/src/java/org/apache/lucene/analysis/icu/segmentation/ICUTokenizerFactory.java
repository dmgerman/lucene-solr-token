begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|util
operator|.
name|ResourceLoader
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|TokenizerFactory
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
name|util
operator|.
name|AttributeFactory
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UScript
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedBreakIterator
import|;
end_import

begin_comment
comment|/**  * Factory for {@link ICUTokenizer}.  * Words are broken across script boundaries, then segmented according to  * the BreakIterator and typing provided by the {@link DefaultICUTokenizerConfig}.  *  *<p>  * To use the default set of per-script rules:  *  *<pre class="prettyprint">  *&lt;fieldType name="text_icu" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.ICUTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  *<p>  * You can customize this tokenizer's behavior by specifying per-script rule files,  * which are compiled by the ICU RuleBasedBreakIterator.  See the  *<a href="http://userguide.icu-project.org/boundaryanalysis#TOC-RBBI-Rules"  *>ICU RuleBasedBreakIterator syntax reference</a>.  *  *<p>  * To add per-script rules, add a "rulefiles" argument, which should contain a  * comma-separated list of<tt>code:rulefile</tt> pairs in the following format:  *<a href="http://unicode.org/iso15924/iso15924-codes.html"  *>four-letter ISO 15924 script code</a>, followed by a colon, then a resource  * path.  E.g. to specify rules for Latin (script code "Latn") and Cyrillic  * (script code "Cyrl"):  *  *<pre class="prettyprint">  *&lt;fieldType name="text_icu_custom" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.ICUTokenizerFactory" cjkAsWords="true"  *                rulefiles="Latn:my.Latin.rules.rbbi,Cyrl:my.Cyrillic.rules.rbbi"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|ICUTokenizerFactory
specifier|public
class|class
name|ICUTokenizerFactory
extends|extends
name|TokenizerFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|RULEFILES
specifier|static
specifier|final
name|String
name|RULEFILES
init|=
literal|"rulefiles"
decl_stmt|;
DECL|field|tailored
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|tailored
decl_stmt|;
DECL|field|config
specifier|private
name|ICUTokenizerConfig
name|config
decl_stmt|;
DECL|field|cjkAsWords
specifier|private
specifier|final
name|boolean
name|cjkAsWords
decl_stmt|;
DECL|field|myanmarAsWords
specifier|private
specifier|final
name|boolean
name|myanmarAsWords
decl_stmt|;
comment|/** Creates a new ICUTokenizerFactory */
DECL|method|ICUTokenizerFactory
specifier|public
name|ICUTokenizerFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|tailored
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|String
name|rulefilesArg
init|=
name|get
argument_list|(
name|args
argument_list|,
name|RULEFILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|rulefilesArg
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|scriptAndResourcePaths
init|=
name|splitFileNames
argument_list|(
name|rulefilesArg
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|scriptAndResourcePath
range|:
name|scriptAndResourcePaths
control|)
block|{
name|int
name|colonPos
init|=
name|scriptAndResourcePath
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|scriptCode
init|=
name|scriptAndResourcePath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colonPos
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|resourcePath
init|=
name|scriptAndResourcePath
operator|.
name|substring
argument_list|(
name|colonPos
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|tailored
operator|.
name|put
argument_list|(
name|UCharacter
operator|.
name|getPropertyValueEnum
argument_list|(
name|UProperty
operator|.
name|SCRIPT
argument_list|,
name|scriptCode
argument_list|)
argument_list|,
name|resourcePath
argument_list|)
expr_stmt|;
block|}
block|}
name|cjkAsWords
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"cjkAsWords"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|myanmarAsWords
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"myanmarAsWords"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|tailored
operator|!=
literal|null
operator|:
literal|"init must be called first!"
assert|;
if|if
condition|(
name|tailored
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|config
operator|=
operator|new
name|DefaultICUTokenizerConfig
argument_list|(
name|cjkAsWords
argument_list|,
name|myanmarAsWords
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BreakIterator
name|breakers
index|[]
init|=
operator|new
name|BreakIterator
index|[
name|UScript
operator|.
name|CODE_LIMIT
index|]
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|tailored
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|code
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|resourcePath
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|breakers
index|[
name|code
index|]
operator|=
name|parseRules
argument_list|(
name|resourcePath
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
name|config
operator|=
operator|new
name|DefaultICUTokenizerConfig
argument_list|(
name|cjkAsWords
argument_list|,
name|myanmarAsWords
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|BreakIterator
name|getBreakIterator
parameter_list|(
name|int
name|script
parameter_list|)
block|{
if|if
condition|(
name|breakers
index|[
name|script
index|]
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|BreakIterator
operator|)
name|breakers
index|[
name|script
index|]
operator|.
name|clone
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getBreakIterator
argument_list|(
name|script
argument_list|)
return|;
block|}
block|}
comment|// TODO: we could also allow codes->types mapping
block|}
expr_stmt|;
block|}
block|}
DECL|method|parseRules
specifier|private
name|BreakIterator
name|parseRules
parameter_list|(
name|String
name|filename
parameter_list|,
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|rules
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|rulesStream
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|rulesStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
name|rules
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|rules
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|RuleBasedBreakIterator
argument_list|(
name|rules
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|ICUTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
assert|assert
name|config
operator|!=
literal|null
operator|:
literal|"inform must be called first!"
assert|;
return|return
operator|new
name|ICUTokenizer
argument_list|(
name|factory
argument_list|,
name|config
argument_list|)
return|;
block|}
block|}
end_class

end_unit

