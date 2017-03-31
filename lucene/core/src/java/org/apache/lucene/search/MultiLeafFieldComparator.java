begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|MultiLeafFieldComparator
specifier|final
class|class
name|MultiLeafFieldComparator
implements|implements
name|LeafFieldComparator
block|{
DECL|field|comparators
specifier|private
specifier|final
name|LeafFieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reverseMul
specifier|private
specifier|final
name|int
index|[]
name|reverseMul
decl_stmt|;
comment|// we extract the first comparator to avoid array access in the common case
comment|// that the first comparator compares worse than the bottom entry in the queue
DECL|field|firstComparator
specifier|private
specifier|final
name|LeafFieldComparator
name|firstComparator
decl_stmt|;
DECL|field|firstReverseMul
specifier|private
specifier|final
name|int
name|firstReverseMul
decl_stmt|;
DECL|method|MultiLeafFieldComparator
name|MultiLeafFieldComparator
parameter_list|(
name|LeafFieldComparator
index|[]
name|comparators
parameter_list|,
name|int
index|[]
name|reverseMul
parameter_list|)
block|{
if|if
condition|(
name|comparators
operator|.
name|length
operator|!=
name|reverseMul
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must have the same number of comparators and reverseMul, got "
operator|+
name|comparators
operator|.
name|length
operator|+
literal|" and "
operator|+
name|reverseMul
operator|.
name|length
argument_list|)
throw|;
block|}
name|this
operator|.
name|comparators
operator|=
name|comparators
expr_stmt|;
name|this
operator|.
name|reverseMul
operator|=
name|reverseMul
expr_stmt|;
name|this
operator|.
name|firstComparator
operator|=
name|comparators
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|firstReverseMul
operator|=
name|reverseMul
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|LeafFieldComparator
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|setBottom
argument_list|(
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|cmp
init|=
name|firstReverseMul
operator|*
name|firstComparator
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|cmp
operator|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compareBottom
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|cmp
init|=
name|firstReverseMul
operator|*
name|firstComparator
operator|.
name|compareTop
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|cmp
operator|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compareTop
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|LeafFieldComparator
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|copy
argument_list|(
name|slot
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|LeafFieldComparator
name|comparator
range|:
name|comparators
control|)
block|{
name|comparator
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
