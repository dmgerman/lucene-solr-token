begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|Detector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|DetectorFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|LangDetectException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|Language
import|;
end_import

begin_comment
comment|/**  * Identifies the language of a set of input fields using http://code.google.com/p/language-detection  *<p>  * See<a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>  * @since 3.5  */
end_comment

begin_class
DECL|class|LangDetectLanguageIdentifierUpdateProcessor
specifier|public
class|class
name|LangDetectLanguageIdentifierUpdateProcessor
extends|extends
name|LanguageIdentifierUpdateProcessor
block|{
DECL|method|LangDetectLanguageIdentifierUpdateProcessor
specifier|public
name|LangDetectLanguageIdentifierUpdateProcessor
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
name|String
name|content
parameter_list|)
block|{
if|if
condition|(
name|content
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// to be consistent with the tika impl?
name|log
operator|.
name|debug
argument_list|(
literal|"No input text to detect language from, returning empty list"
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
try|try
block|{
name|Detector
name|detector
init|=
name|DetectorFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|detector
operator|.
name|append
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Language
argument_list|>
name|langlist
init|=
name|detector
operator|.
name|getProbabilities
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|DetectedLanguage
argument_list|>
name|solrLangList
init|=
operator|new
name|ArrayList
argument_list|<
name|DetectedLanguage
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Language
name|l
range|:
name|langlist
control|)
block|{
name|solrLangList
operator|.
name|add
argument_list|(
operator|new
name|DetectedLanguage
argument_list|(
name|l
operator|.
name|lang
argument_list|,
name|l
operator|.
name|prob
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|solrLangList
return|;
block|}
catch|catch
parameter_list|(
name|LangDetectException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not determine language, returning empty list: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

