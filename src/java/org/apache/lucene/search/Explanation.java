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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2003 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/** Expert: Describes the score computation for document and query. */
end_comment

begin_class
DECL|class|Explanation
specifier|public
class|class
name|Explanation
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|value
specifier|private
name|float
name|value
decl_stmt|;
comment|// the value of this node
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
comment|// what it represents
DECL|field|details
specifier|private
name|ArrayList
name|details
decl_stmt|;
comment|// sub-explanations
DECL|method|Explanation
specifier|public
name|Explanation
parameter_list|()
block|{}
DECL|method|Explanation
specifier|public
name|Explanation
parameter_list|(
name|float
name|value
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/** The value assigned to this explanation node. */
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/** Sets the value assigned to this explanation node. */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/** A description of this explanation node. */
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/** Sets the description of this explanation node. */
DECL|method|setDescription
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/** The sub-nodes of this explanation node. */
DECL|method|getDetails
specifier|public
name|Explanation
index|[]
name|getDetails
parameter_list|()
block|{
if|if
condition|(
name|details
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Explanation
index|[]
operator|)
name|details
operator|.
name|toArray
argument_list|(
operator|new
name|Explanation
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** Adds a sub-node to this explanation node. */
DECL|method|addDetail
specifier|public
name|void
name|addDetail
parameter_list|(
name|Explanation
name|detail
parameter_list|)
block|{
if|if
condition|(
name|details
operator|==
literal|null
condition|)
name|details
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
name|detail
argument_list|)
expr_stmt|;
block|}
comment|/** Render an explanation as HTML. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|toString
specifier|private
name|String
name|toString
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|Explanation
index|[]
name|details
init|=
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|details
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|details
index|[
name|i
index|]
operator|.
name|toString
argument_list|(
name|depth
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Render an explanation as HTML. */
DECL|method|toHtml
specifier|public
name|String
name|toHtml
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"<ul>\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"<li>"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"</li>\n"
argument_list|)
expr_stmt|;
name|Explanation
index|[]
name|details
init|=
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|details
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|details
index|[
name|i
index|]
operator|.
name|toHtml
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"</ul>\n"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

