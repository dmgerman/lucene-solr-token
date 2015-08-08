begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|TimeOut
specifier|public
class|class
name|TimeOut
block|{
DECL|field|timeoutAt
specifier|private
specifier|final
name|long
name|timeoutAt
decl_stmt|;
DECL|method|TimeOut
specifier|public
name|TimeOut
parameter_list|(
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|timeoutAt
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|interval
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
DECL|method|hasTimedOut
specifier|public
name|boolean
name|hasTimedOut
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|>
name|timeoutAt
return|;
block|}
DECL|method|timeLeft
specifier|public
name|long
name|timeLeft
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|unit
operator|.
name|convert
argument_list|(
name|timeoutAt
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
block|}
end_class

end_unit

