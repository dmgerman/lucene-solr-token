begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|language
operator|.
name|LanguageIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Identifies the language of a set of input fields using Tika's  * LanguageIdentifier.  * The tika-core-x.y.jar must be on the classpath  *<p>  * See<a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>  * @since 3.5  */
end_comment

begin_class
DECL|class|TikaLanguageIdentifierUpdateProcessor
specifier|public
class|class
name|TikaLanguageIdentifierUpdateProcessor
extends|extends
name|LanguageIdentifierUpdateProcessor
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|TikaLanguageIdentifierUpdateProcessor
specifier|public
name|TikaLanguageIdentifierUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|detectLanguage
specifier|protected
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|detectLanguage
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|List
argument_list|<
name|DetectedLanguage
argument_list|>
name|languages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|concatFields
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LanguageIdentifier
name|identifier
init|=
operator|new
name|LanguageIdentifier
argument_list|(
name|content
argument_list|)
decl_stmt|;
comment|// FIXME: Hack - we get the distance from toString and calculate our own certainty score
name|Double
name|distance
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|tikaSimilarityPattern
operator|.
name|matcher
argument_list|(
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"$1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// This formula gives: 0.02 => 0.8, 0.1 => 0.5 which is a better sweetspot than isReasonablyCertain()
name|Double
name|certainty
init|=
literal|1
operator|-
operator|(
literal|5
operator|*
name|distance
operator|)
decl_stmt|;
if|if
condition|(
name|certainty
operator|<
literal|0
condition|)
name|certainty
operator|=
literal|0d
expr_stmt|;
name|DetectedLanguage
name|language
init|=
operator|new
name|DetectedLanguage
argument_list|(
name|identifier
operator|.
name|getLanguage
argument_list|()
argument_list|,
name|certainty
argument_list|)
decl_stmt|;
name|languages
operator|.
name|add
argument_list|(
name|language
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Language detected as "
operator|+
name|language
operator|+
literal|" with a certainty of "
operator|+
name|language
operator|.
name|getCertainty
argument_list|()
operator|+
literal|" (Tika distance="
operator|+
name|identifier
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No input text to detect language from, returning empty list"
argument_list|)
expr_stmt|;
block|}
return|return
name|languages
return|;
block|}
comment|/**    * Concatenates content from multiple fields    */
DECL|method|concatFields
specifier|protected
name|String
name|concatFields
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|getExpectedSize
argument_list|(
name|doc
argument_list|,
name|inputFields
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|inputFields
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Appending field "
operator|+
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|fieldValues
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldValues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|content
range|:
name|fieldValues
control|)
block|{
if|if
condition|(
name|content
operator|instanceof
name|String
condition|)
block|{
name|String
name|stringContent
init|=
operator|(
name|String
operator|)
name|content
decl_stmt|;
if|if
condition|(
name|stringContent
operator|.
name|length
argument_list|()
operator|>
name|maxFieldValueChars
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|stringContent
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|maxFieldValueChars
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|stringContent
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
name|maxTotalChars
condition|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
name|maxTotalChars
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Field "
operator|+
name|fieldName
operator|+
literal|" not a String value, not including in detection"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Calculate expected string size.    *    * @param doc           solr input document    * @param fields        fields to select    * @return expected size of string value    */
DECL|method|getExpectedSize
specifier|private
name|int
name|getExpectedSize
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|String
index|[]
name|fields
parameter_list|)
block|{
name|int
name|docSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|contents
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|content
range|:
name|contents
control|)
block|{
if|if
condition|(
name|content
operator|instanceof
name|String
condition|)
block|{
name|docSize
operator|+=
name|Math
operator|.
name|min
argument_list|(
operator|(
operator|(
name|String
operator|)
name|content
operator|)
operator|.
name|length
argument_list|()
argument_list|,
name|maxFieldValueChars
argument_list|)
expr_stmt|;
block|}
block|}
name|docSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|docSize
argument_list|,
name|maxTotalChars
argument_list|)
expr_stmt|;
block|}
return|return
name|docSize
return|;
block|}
block|}
end_class

end_unit

