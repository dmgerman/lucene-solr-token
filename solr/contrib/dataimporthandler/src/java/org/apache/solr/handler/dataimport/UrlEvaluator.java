begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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

begin_comment
comment|/**  *<p>Escapes reserved characters in Solr queries</p>  *  * @see org.apache.solr.client.solrj.util.ClientUtils#escapeQueryChars(String)  */
end_comment

begin_class
DECL|class|UrlEvaluator
specifier|public
class|class
name|UrlEvaluator
extends|extends
name|Evaluator
block|{
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'encodeUrl' must have at least one parameter "
argument_list|)
throw|;
block|}
name|String
name|s
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|s
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to encode expression: "
operator|+
name|expression
operator|+
literal|" with value: "
operator|+
name|s
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

