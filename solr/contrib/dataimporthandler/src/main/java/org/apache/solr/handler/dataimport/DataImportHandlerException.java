begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/**  *<p> Exception class for all DataImportHandler exceptions</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *<p/>  * $Id$  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DataImportHandlerException
specifier|public
class|class
name|DataImportHandlerException
extends|extends
name|RuntimeException
block|{
DECL|field|errCode
specifier|private
name|int
name|errCode
decl_stmt|;
DECL|field|debugged
specifier|public
name|boolean
name|debugged
init|=
literal|false
decl_stmt|;
DECL|field|SEVERE
DECL|field|WARN
DECL|field|SKIP
DECL|field|SKIP_ROW
specifier|public
specifier|static
specifier|final
name|int
name|SEVERE
init|=
literal|500
decl_stmt|,
name|WARN
init|=
literal|400
decl_stmt|,
name|SKIP
init|=
literal|300
decl_stmt|,
name|SKIP_ROW
init|=
literal|301
decl_stmt|;
DECL|method|DataImportHandlerException
specifier|public
name|DataImportHandlerException
parameter_list|(
name|int
name|err
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|errCode
operator|=
name|err
expr_stmt|;
block|}
DECL|method|DataImportHandlerException
specifier|public
name|DataImportHandlerException
parameter_list|(
name|int
name|err
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|+
operator|(
name|SolrWriter
operator|.
name|getDocCount
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|MSG
operator|+
name|SolrWriter
operator|.
name|getDocCount
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|errCode
operator|=
name|err
expr_stmt|;
block|}
DECL|method|DataImportHandlerException
specifier|public
name|DataImportHandlerException
parameter_list|(
name|int
name|err
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|+
operator|(
name|SolrWriter
operator|.
name|getDocCount
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|MSG
operator|+
name|SolrWriter
operator|.
name|getDocCount
argument_list|()
operator|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|errCode
operator|=
name|err
expr_stmt|;
block|}
DECL|method|DataImportHandlerException
specifier|public
name|DataImportHandlerException
parameter_list|(
name|int
name|err
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|errCode
operator|=
name|err
expr_stmt|;
block|}
DECL|method|getErrCode
specifier|public
name|int
name|getErrCode
parameter_list|()
block|{
return|return
name|errCode
return|;
block|}
DECL|method|wrapAndThrow
specifier|public
specifier|static
name|void
name|wrapAndThrow
parameter_list|(
name|int
name|err
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|DataImportHandlerException
condition|)
block|{
throw|throw
operator|(
name|DataImportHandlerException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|err
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|wrapAndThrow
specifier|public
specifier|static
name|void
name|wrapAndThrow
parameter_list|(
name|int
name|err
parameter_list|,
name|Exception
name|e
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|DataImportHandlerException
condition|)
block|{
throw|throw
operator|(
name|DataImportHandlerException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|err
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|MSG
specifier|public
specifier|static
specifier|final
name|String
name|MSG
init|=
literal|" Processing Document # "
decl_stmt|;
block|}
end_class

end_unit

