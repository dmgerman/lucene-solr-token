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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|/** A PriorityQueue maintains a partial ordering of its elements such that the   least element can always be found in constant time.  Put()'s and pop()'s   require log(size) time. */
end_comment

begin_class
DECL|class|PriorityQueue
specifier|public
specifier|abstract
class|class
name|PriorityQueue
block|{
DECL|field|heap
specifier|private
name|Object
index|[]
name|heap
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/** Determines the ordering of objects in this priority queue.  Subclasses     must define this one method. */
DECL|method|lessThan
specifier|protected
specifier|abstract
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
function_decl|;
comment|/** Subclass constructors must call this. */
DECL|method|initialize
specifier|protected
specifier|final
name|void
name|initialize
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|int
name|heapSize
init|=
operator|(
name|maxSize
operator|*
literal|2
operator|)
operator|+
literal|1
decl_stmt|;
name|heap
operator|=
operator|new
name|Object
index|[
name|heapSize
index|]
expr_stmt|;
block|}
comment|/** Adds an Object to a PriorityQueue in log(size) time. */
DECL|method|put
specifier|public
specifier|final
name|void
name|put
parameter_list|(
name|Object
name|element
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
name|heap
index|[
name|size
index|]
operator|=
name|element
expr_stmt|;
name|upHeap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the least element of the PriorityQueue in constant time. */
DECL|method|top
specifier|public
specifier|final
name|Object
name|top
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
return|return
name|heap
index|[
literal|1
index|]
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/** Removes and returns the least element of the PriorityQueue in log(size)     time. */
DECL|method|pop
specifier|public
specifier|final
name|Object
name|pop
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|Object
name|result
init|=
name|heap
index|[
literal|1
index|]
decl_stmt|;
comment|// save first value
name|heap
index|[
literal|1
index|]
operator|=
name|heap
index|[
name|size
index|]
expr_stmt|;
comment|// move last to first
name|heap
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
comment|// permit GC of objects
name|size
operator|--
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
comment|// adjust heap
return|return
name|result
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
comment|/** Should be called when the Object at top changes values.  Still log(n)    * worst case, but it's at least twice as fast to<pre>    *  { pq.top().change(); pq.adjustTop(); }    *</pre> instead of<pre>    *  { o = pq.pop(); o.change(); pq.push(o); }    *</pre>    */
DECL|method|adjustTop
specifier|public
specifier|final
name|void
name|adjustTop
parameter_list|()
block|{
name|downHeap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of elements currently stored in the PriorityQueue. */
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** Removes all entries from the PriorityQueue. */
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
name|heap
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|upHeap
specifier|private
specifier|final
name|void
name|upHeap
parameter_list|()
block|{
name|int
name|i
init|=
name|size
decl_stmt|;
name|Object
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save bottom node
name|int
name|j
init|=
name|i
operator|>>>
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|>
literal|0
operator|&&
name|lessThan
argument_list|(
name|node
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift parents down
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|j
operator|>>>
literal|1
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
DECL|method|downHeap
specifier|private
specifier|final
name|void
name|downHeap
parameter_list|()
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
name|Object
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save top node
name|int
name|j
init|=
name|i
operator|<<
literal|1
decl_stmt|;
comment|// find smaller child
name|int
name|k
init|=
name|j
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
while|while
condition|(
name|j
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|j
index|]
argument_list|,
name|node
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift up child
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|i
operator|<<
literal|1
expr_stmt|;
name|k
operator|=
name|j
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
block|}
end_class

end_unit

