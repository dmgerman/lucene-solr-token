begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io
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
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_class
DECL|class|DescMetricComp
specifier|public
class|class
name|DescMetricComp
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
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
DECL|field|ord
specifier|private
name|int
name|ord
decl_stmt|;
DECL|method|DescMetricComp
specifier|public
name|DescMetricComp
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|this
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|t1
parameter_list|,
name|Tuple
name|t2
parameter_list|)
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|values1
init|=
operator|(
name|List
argument_list|<
name|Double
argument_list|>
operator|)
name|t1
operator|.
name|get
argument_list|(
literal|"metricValues"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|values2
init|=
operator|(
name|List
argument_list|<
name|Double
argument_list|>
operator|)
name|t2
operator|.
name|get
argument_list|(
literal|"metricValues"
argument_list|)
decl_stmt|;
return|return
name|values1
operator|.
name|get
argument_list|(
name|ord
argument_list|)
operator|.
name|compareTo
argument_list|(
name|values2
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
operator|*
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

