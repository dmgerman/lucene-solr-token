begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|el
operator|.
name|GreekCharsets
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
name|el
operator|.
name|GreekLowerCaseFilter
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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

begin_class
DECL|class|GreekLowerCaseFilterFactory
specifier|public
class|class
name|GreekLowerCaseFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
annotation|@
name|Deprecated
DECL|field|CHARSETS
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
name|CHARSETS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"UnicodeGreek"
argument_list|,
name|GreekCharsets
operator|.
name|UnicodeGreek
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"ISO"
argument_list|,
name|GreekCharsets
operator|.
name|ISO
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"CP1253"
argument_list|,
name|GreekCharsets
operator|.
name|CP1253
argument_list|)
expr_stmt|;
block|}
DECL|field|charset
specifier|private
name|char
index|[]
name|charset
init|=
name|GreekCharsets
operator|.
name|UnicodeGreek
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GreekLowerCaseFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
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
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|charsetName
init|=
name|args
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|charsetName
condition|)
block|{
name|charset
operator|=
name|CHARSETS
operator|.
name|get
argument_list|(
name|charsetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|charset
operator|.
name|equals
argument_list|(
name|GreekCharsets
operator|.
name|UnicodeGreek
argument_list|)
condition|)
name|logger
operator|.
name|warn
argument_list|(
literal|"Specifying UnicodeGreek is no longer required (default).  "
operator|+
literal|"Use of the charset parameter will cause an error in Solr 1.5"
argument_list|)
expr_stmt|;
else|else
name|logger
operator|.
name|warn
argument_list|(
literal|"Support for this custom encoding is deprecated.  "
operator|+
literal|"Use of the charset parameter will cause an error in Solr 1.5"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|charset
operator|=
name|GreekCharsets
operator|.
name|UnicodeGreek
expr_stmt|;
comment|/* default to unicode */
block|}
if|if
condition|(
literal|null
operator|==
name|charset
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Don't understand charset: "
operator|+
name|charsetName
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
name|GreekLowerCaseFilter
name|create
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
return|return
operator|new
name|GreekLowerCaseFilter
argument_list|(
name|in
argument_list|,
name|charset
argument_list|)
return|;
block|}
block|}
end_class

end_unit

