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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|java
operator|.
name|util
operator|.
name|Vector
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
name|*
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
name|index
operator|.
name|*
import|;
end_import

begin_class
DECL|class|SloppyPhraseScorer
specifier|final
class|class
name|SloppyPhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
DECL|method|SloppyPhraseScorer
name|SloppyPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|int
name|slop
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|tps
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
block|}
DECL|method|phraseFreq
specifier|protected
specifier|final
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|end
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// build pq from list
block|}
name|float
name|freq
init|=
literal|0.0f
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
do|do
block|{
name|PhrasePositions
name|pp
init|=
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|pp
operator|.
name|position
decl_stmt|;
name|int
name|next
init|=
operator|(
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|position
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
name|start
init|;
name|pos
operator|<=
name|next
condition|;
name|pos
operator|=
name|pp
operator|.
name|position
control|)
block|{
name|start
operator|=
name|pos
expr_stmt|;
comment|// advance pp to min window
if|if
condition|(
operator|!
name|pp
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
comment|// ran out of a term -- done
break|break;
block|}
block|}
name|int
name|matchLength
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|matchLength
operator|<=
name|slop
condition|)
name|freq
operator|+=
name|getSimilarity
argument_list|()
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
comment|// score match
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// restore pq
block|}
do|while
condition|(
operator|!
name|done
condition|)
do|;
return|return
name|freq
return|;
block|}
block|}
end_class

end_unit

