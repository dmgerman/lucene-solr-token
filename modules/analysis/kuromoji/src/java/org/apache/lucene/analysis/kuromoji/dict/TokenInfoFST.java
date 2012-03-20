begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FST
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|FST
operator|.
name|Arc
import|;
end_import

begin_comment
comment|/**  * Thin wrapper around an FST with root-arc caching for Japanese.  *<p>  * Depending upon fasterButMoreRam, either just kana (191 arcs),  * or kana and han (28,607 arcs) are cached. The latter offers  * additional performance at the cost of more RAM.  */
end_comment

begin_class
DECL|class|TokenInfoFST
specifier|public
specifier|final
class|class
name|TokenInfoFST
block|{
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
comment|// depending upon fasterButMoreRam, we cache root arcs for either
comment|// kana (0x3040-0x30FF) or kana + han (0x3040-0x9FFF)
comment|// false: 191 arcs
comment|// true:  28,607 arcs (costs ~1.5MB)
DECL|field|cacheCeiling
specifier|private
specifier|final
name|int
name|cacheCeiling
decl_stmt|;
DECL|field|rootCache
specifier|private
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|rootCache
index|[]
decl_stmt|;
DECL|field|NO_OUTPUT
specifier|public
specifier|final
name|Long
name|NO_OUTPUT
decl_stmt|;
DECL|method|TokenInfoFST
specifier|public
name|TokenInfoFST
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|,
name|boolean
name|fasterButMoreRam
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|this
operator|.
name|cacheCeiling
operator|=
name|fasterButMoreRam
condition|?
literal|0x9FFF
else|:
literal|0x30FF
expr_stmt|;
name|NO_OUTPUT
operator|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
name|rootCache
operator|=
name|cacheRootArcs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cacheRootArcs
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
index|[]
name|cacheRootArcs
parameter_list|()
throws|throws
name|IOException
block|{
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|rootCache
index|[]
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
literal|1
operator|+
operator|(
name|cacheCeiling
operator|-
literal|0x3040
operator|)
index|]
decl_stmt|;
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|firstArc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|firstArc
argument_list|)
expr_stmt|;
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// TODO: jump to 3040, readNextRealArc to ceiling? (just be careful we don't add bugs)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rootCache
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
literal|0x3040
operator|+
name|i
argument_list|,
name|firstArc
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|rootCache
index|[
name|i
index|]
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rootCache
return|;
block|}
DECL|method|findTargetArc
specifier|public
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|findTargetArc
parameter_list|(
name|int
name|ch
parameter_list|,
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|follow
parameter_list|,
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
parameter_list|,
name|boolean
name|useCache
parameter_list|,
name|FST
operator|.
name|BytesReader
name|fstReader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|useCache
operator|&&
name|ch
operator|>=
literal|0x3040
operator|&&
name|ch
operator|<=
name|cacheCeiling
condition|)
block|{
assert|assert
name|ch
operator|!=
name|FST
operator|.
name|END_LABEL
assert|;
specifier|final
name|Arc
argument_list|<
name|Long
argument_list|>
name|result
init|=
name|rootCache
index|[
name|ch
operator|-
literal|0x3040
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|arc
operator|.
name|copyFrom
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|arc
return|;
block|}
block|}
else|else
block|{
return|return
name|fst
operator|.
name|findTargetArc
argument_list|(
name|ch
argument_list|,
name|follow
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
return|;
block|}
block|}
DECL|method|getFirstArc
specifier|public
name|Arc
argument_list|<
name|Long
argument_list|>
name|getFirstArc
parameter_list|(
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
parameter_list|)
block|{
return|return
name|fst
operator|.
name|getFirstArc
argument_list|(
name|arc
argument_list|)
return|;
block|}
DECL|method|getBytesReader
specifier|public
name|FST
operator|.
name|BytesReader
name|getBytesReader
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|fst
operator|.
name|getBytesReader
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/** @lucene.internal for testing only */
DECL|method|getInternalFST
name|FST
argument_list|<
name|Long
argument_list|>
name|getInternalFST
parameter_list|()
block|{
return|return
name|fst
return|;
block|}
block|}
end_class

end_unit

