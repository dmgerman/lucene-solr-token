begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
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
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/* * */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import

begin_class
DECL|class|Bucket
specifier|public
class|class
name|Bucket
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|bucketKey
specifier|private
name|String
name|bucketKey
decl_stmt|;
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|()
block|{    }
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|String
name|bucketKey
parameter_list|)
block|{
name|this
operator|.
name|bucketKey
operator|=
name|bucketKey
expr_stmt|;
block|}
DECL|method|getBucketValue
specifier|public
name|String
name|getBucketValue
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
return|return
name|tuple
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|bucketKey
return|;
block|}
block|}
end_class

end_unit

