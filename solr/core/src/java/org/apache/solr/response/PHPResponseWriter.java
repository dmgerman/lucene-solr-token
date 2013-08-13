begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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

begin_class
DECL|class|PHPResponseWriter
specifier|public
class|class
name|PHPResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|CONTENT_TYPE_PHP_UTF8
specifier|static
name|String
name|CONTENT_TYPE_PHP_UTF8
init|=
literal|"text/x-php;charset=UTF-8"
decl_stmt|;
DECL|field|contentType
specifier|private
name|String
name|contentType
init|=
name|CONTENT_TYPE_PHP_UTF8
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|namedList
parameter_list|)
block|{
name|String
name|contentType
init|=
operator|(
name|String
operator|)
name|namedList
operator|.
name|get
argument_list|(
literal|"content-type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|PHPWriter
name|w
init|=
operator|new
name|PHPWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|contentType
return|;
block|}
block|}
end_class

begin_class
DECL|class|PHPWriter
class|class
name|PHPWriter
extends|extends
name|JSONWriter
block|{
DECL|method|PHPWriter
specifier|public
name|PHPWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNamedList
specifier|public
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNamedListAsMapMangled
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapOpener
specifier|public
name|void
name|writeMapOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"array("
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapCloser
specifier|public
name|void
name|writeMapCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayOpener
specifier|public
name|void
name|writeArrayOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"array("
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayCloser
specifier|public
name|void
name|writeArrayCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeKey
specifier|protected
name|void
name|writeKey
parameter_list|(
name|String
name|fname
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStr
argument_list|(
literal|null
argument_list|,
name|fname
argument_list|,
name|needsEscaping
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStr
specifier|public
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|needsEscaping
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'\''
case|:
case|case
literal|'\\'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
default|default:
name|writer
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

