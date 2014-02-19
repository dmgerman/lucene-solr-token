begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestTimSorter
specifier|public
class|class
name|TestTimSorter
extends|extends
name|BaseSortTestCase
block|{
DECL|method|TestTimSorter
specifier|public
name|TestTimSorter
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSorter
specifier|public
name|Sorter
name|newSorter
parameter_list|(
name|Entry
index|[]
name|arr
parameter_list|)
block|{
return|return
operator|new
name|ArrayTimSorter
argument_list|<
name|Entry
argument_list|>
argument_list|(
name|arr
argument_list|,
name|ArrayUtil
operator|.
expr|<
name|Entry
operator|>
name|naturalComparator
argument_list|()
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

