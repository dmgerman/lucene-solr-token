begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

begin_comment
comment|/** Exception to catch all types of communication / parsing issues associated with talking to SOLR  *   *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrServerException
specifier|public
class|class
name|SolrServerException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3371703521752000294L
decl_stmt|;
DECL|method|SolrServerException
specifier|public
name|SolrServerException
parameter_list|(
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
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrServerException
specifier|public
name|SolrServerException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrServerException
specifier|public
name|SolrServerException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|getRootCause
specifier|public
name|Throwable
name|getRootCause
parameter_list|()
block|{
name|Throwable
name|t
init|=
name|this
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Throwable
name|cause
init|=
name|t
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
name|t
operator|=
name|cause
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

