begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

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
name|PriorityQueue
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|IndexReader
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
name|Term
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
name|TermEnum
import|;
end_import

begin_comment
comment|/**  *<code>HighFreqTerms</code> class extracts terms and their frequencies out  * of an existing Lucene index.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|HighFreqTerms
specifier|public
class|class
name|HighFreqTerms
block|{
DECL|field|numTerms
specifier|public
specifier|static
name|int
name|numTerms
init|=
literal|100
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|TermInfoQueue
name|tiq
init|=
operator|new
name|TermInfoQueue
argument_list|(
name|numTerms
argument_list|)
decl_stmt|;
name|TermEnum
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|int
name|minFreq
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|docFreq
argument_list|()
operator|>
name|minFreq
condition|)
block|{
name|tiq
operator|.
name|put
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|terms
operator|.
name|docFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tiq
operator|.
name|size
argument_list|()
operator|>
name|numTerms
condition|)
comment|// if tiq overfull
block|{
name|tiq
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// remove lowest in tiq
name|minFreq
operator|=
operator|(
operator|(
name|TermInfo
operator|)
name|tiq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|docFreq
expr_stmt|;
comment|// reset minFreq
block|}
block|}
block|}
while|while
condition|(
name|tiq
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|TermInfo
name|termInfo
init|=
operator|(
name|TermInfo
operator|)
name|tiq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|termInfo
operator|.
name|term
operator|+
literal|" "
operator|+
name|termInfo
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n"
operator|+
literal|"java org.apache.lucene.misc.HighFreqTerms<index dir>\n\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|TermInfo
specifier|final
class|class
name|TermInfo
block|{
DECL|method|TermInfo
name|TermInfo
parameter_list|(
name|Term
name|t
parameter_list|,
name|int
name|df
parameter_list|)
block|{
name|term
operator|=
name|t
expr_stmt|;
name|docFreq
operator|=
name|df
expr_stmt|;
block|}
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
DECL|field|term
name|Term
name|term
decl_stmt|;
block|}
end_class

begin_class
DECL|class|TermInfoQueue
specifier|final
class|class
name|TermInfoQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|TermInfoQueue
name|TermInfoQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
name|TermInfo
name|termInfoA
init|=
operator|(
name|TermInfo
operator|)
name|a
decl_stmt|;
name|TermInfo
name|termInfoB
init|=
operator|(
name|TermInfo
operator|)
name|b
decl_stmt|;
return|return
name|termInfoA
operator|.
name|docFreq
operator|<
name|termInfoB
operator|.
name|docFreq
return|;
block|}
block|}
end_class

end_unit

