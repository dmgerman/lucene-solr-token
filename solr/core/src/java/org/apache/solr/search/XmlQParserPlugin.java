begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
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
name|search
operator|.
name|Query
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|NamedList
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
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_class
DECL|class|XmlQParserPlugin
specifier|public
class|class
name|XmlQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"xmlparser"
decl_stmt|;
DECL|field|args
specifier|private
name|NamedList
name|args
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|class|XmlQParser
specifier|private
class|class
name|XmlQParser
extends|extends
name|QParser
block|{
DECL|method|XmlQParser
specifier|public
name|XmlQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
specifier|final
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qstr
operator|==
literal|null
operator|||
name|qstr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|String
name|defaultField
init|=
name|getParam
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
decl_stmt|;
specifier|final
name|Analyzer
name|analyzer
init|=
name|schema
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
specifier|final
name|SolrCoreParser
name|solrParser
init|=
operator|new
name|SolrCoreParser
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|solrParser
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|solrParser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|qstr
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParserException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" in "
operator|+
name|req
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|XmlQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
block|}
end_class

end_unit

