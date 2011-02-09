begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * A {@link Scorer} which wraps another scorer and caches the score of the  * current document. Successive calls to {@link #score()} will return the same  * result and will not invoke the wrapped Scorer's score() method, unless the  * current document has changed.<br>  * This class might be useful due to the changes done to the {@link Collector}  * interface, in which the score is not computed for a document by default, only  * if the collector requests it. Some collectors may need to use the score in  * several places, however all they have in hand is a {@link Scorer} object, and  * might end up computing the score of a document more than once.  */
end_comment

begin_class
DECL|class|ScoreCachingWrappingScorer
specifier|public
class|class
name|ScoreCachingWrappingScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|curDoc
specifier|private
name|int
name|curDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|curScore
specifier|private
name|float
name|curScore
decl_stmt|;
comment|/** Creates a new instance by wrapping the given scorer. */
DECL|method|ScoreCachingWrappingScorer
specifier|public
name|ScoreCachingWrappingScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|super
argument_list|(
name|scorer
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|max
argument_list|,
name|firstDocID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|curDoc
condition|)
block|{
name|curScore
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|curDoc
operator|=
name|doc
expr_stmt|;
block|}
return|return
name|curScore
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
end_class

end_unit

