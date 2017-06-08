begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MockTimerSupplier
specifier|public
class|class
name|MockTimerSupplier
implements|implements
name|MetricRegistry
operator|.
name|MetricSupplier
argument_list|<
name|Timer
argument_list|>
block|{
DECL|field|boolParam
specifier|public
name|boolean
name|boolParam
decl_stmt|;
DECL|field|strParam
specifier|public
name|String
name|strParam
decl_stmt|;
DECL|field|intParam
specifier|public
name|int
name|intParam
decl_stmt|;
DECL|method|setBoolParam
specifier|public
name|void
name|setBoolParam
parameter_list|(
name|boolean
name|boolParam
parameter_list|)
block|{
name|this
operator|.
name|boolParam
operator|=
name|boolParam
expr_stmt|;
block|}
DECL|method|setStrParam
specifier|public
name|void
name|setStrParam
parameter_list|(
name|String
name|strParam
parameter_list|)
block|{
name|this
operator|.
name|strParam
operator|=
name|strParam
expr_stmt|;
block|}
DECL|method|setIntParam
specifier|public
name|void
name|setIntParam
parameter_list|(
name|int
name|intParam
parameter_list|)
block|{
name|this
operator|.
name|intParam
operator|=
name|intParam
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newMetric
specifier|public
name|Timer
name|newMetric
parameter_list|()
block|{
return|return
operator|new
name|Timer
argument_list|()
return|;
block|}
block|}
end_class

end_unit
