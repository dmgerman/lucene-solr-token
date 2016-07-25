begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestIntroSelector
specifier|public
class|class
name|TestIntroSelector
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSelect
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
condition|;
operator|++
name|iter
control|)
block|{
name|doTestSelect
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSlowSelect
specifier|public
name|void
name|testSlowSelect
parameter_list|()
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
condition|;
operator|++
name|iter
control|)
block|{
name|doTestSelect
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestSelect
specifier|private
name|void
name|doTestSelect
parameter_list|(
name|boolean
name|slow
parameter_list|)
block|{
specifier|final
name|int
name|from
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|to
init|=
name|from
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|Integer
index|[]
name|arr
init|=
operator|new
name|Integer
index|[
name|from
operator|+
name|to
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arr
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|k
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|from
argument_list|,
name|to
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Integer
index|[]
name|expected
init|=
name|arr
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|Integer
index|[]
name|actual
init|=
name|arr
operator|.
name|clone
argument_list|()
decl_stmt|;
name|IntroSelector
name|selector
init|=
operator|new
name|IntroSelector
argument_list|()
block|{
name|Integer
name|pivot
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|ArrayUtil
operator|.
name|swap
argument_list|(
name|actual
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|actual
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
return|return
name|pivot
operator|.
name|compareTo
argument_list|(
name|actual
index|[
name|j
index|]
argument_list|)
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|slow
condition|)
block|{
name|selector
operator|.
name|slowSelect
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|selector
operator|.
name|select
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
index|[
name|k
index|]
argument_list|,
name|actual
index|[
name|k
index|]
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
name|actual
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|<
name|from
operator|||
name|i
operator|>=
name|to
condition|)
block|{
name|assertSame
argument_list|(
name|arr
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<=
name|k
condition|)
block|{
name|assertTrue
argument_list|(
name|actual
index|[
name|i
index|]
operator|.
name|intValue
argument_list|()
operator|<=
name|actual
index|[
name|k
index|]
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|actual
index|[
name|i
index|]
operator|.
name|intValue
argument_list|()
operator|>=
name|actual
index|[
name|k
index|]
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

