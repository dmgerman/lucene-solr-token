begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|Languages
operator|.
name|LanguageSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|NameType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|PhoneticEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|RuleType
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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**   * Factory for {@link BeiderMorseFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_bm" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.BeiderMorseFilterFactory"  *        nameType="GENERIC" ruleType="APPROX"   *        concat="true" languageSet="auto"  *&lt;/filter&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|BeiderMorseFilterFactory
specifier|public
class|class
name|BeiderMorseFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|engine
specifier|private
specifier|final
name|PhoneticEngine
name|engine
decl_stmt|;
DECL|field|languageSet
specifier|private
specifier|final
name|LanguageSet
name|languageSet
decl_stmt|;
comment|/** Creates a new BeiderMorseFilterFactory */
DECL|method|BeiderMorseFilterFactory
specifier|public
name|BeiderMorseFilterFactory
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
comment|// PhoneticEngine = NameType + RuleType + concat
comment|// we use common-codec's defaults: GENERIC + APPROX + true
name|NameType
name|nameType
init|=
name|NameType
operator|.
name|valueOf
argument_list|(
name|get
argument_list|(
name|args
argument_list|,
literal|"nameType"
argument_list|,
name|NameType
operator|.
name|GENERIC
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RuleType
name|ruleType
init|=
name|RuleType
operator|.
name|valueOf
argument_list|(
name|get
argument_list|(
name|args
argument_list|,
literal|"ruleType"
argument_list|,
name|RuleType
operator|.
name|APPROX
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|concat
init|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"concat"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|engine
operator|=
operator|new
name|PhoneticEngine
argument_list|(
name|nameType
argument_list|,
name|ruleType
argument_list|,
name|concat
argument_list|)
expr_stmt|;
comment|// LanguageSet: defaults to automagic, otherwise a comma-separated list.
name|Set
argument_list|<
name|String
argument_list|>
name|langs
init|=
name|getSet
argument_list|(
name|args
argument_list|,
literal|"languageSet"
argument_list|)
decl_stmt|;
name|languageSet
operator|=
operator|(
literal|null
operator|==
name|langs
operator|||
operator|(
literal|1
operator|==
name|langs
operator|.
name|size
argument_list|()
operator|&&
name|langs
operator|.
name|contains
argument_list|(
literal|"auto"
argument_list|)
operator|)
operator|)
condition|?
literal|null
else|:
name|LanguageSet
operator|.
name|from
argument_list|(
name|langs
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
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|BeiderMorseFilter
argument_list|(
name|input
argument_list|,
name|engine
argument_list|,
name|languageSet
argument_list|)
return|;
block|}
block|}
end_class

end_unit

