begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  *   * Copyright 1997 Hewlett-Packard Company  *   * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment

begin_package
DECL|package|hplb.xml
package|package
name|hplb
operator|.
name|xml
package|;
end_package

begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *   * @author  Anders Kristensen  */
end_comment

begin_class
DECL|class|NodeListImpl
specifier|public
class|class
name|NodeListImpl
block|{
DECL|field|elms
specifier|protected
name|Node
index|[]
name|elms
decl_stmt|;
DECL|field|count
specifier|protected
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|NodeListImpl
specifier|public
name|NodeListImpl
parameter_list|()
block|{
name|this
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|NodeListImpl
specifier|public
name|NodeListImpl
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Initial size of must be at least 1"
argument_list|)
throw|;
name|elms
operator|=
operator|new
name|Node
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|method|add
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|int
name|len
init|=
name|elms
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|len
operator|==
name|count
condition|)
block|{
name|Node
index|[]
name|e
init|=
operator|new
name|Node
index|[
name|len
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
literal|0
argument_list|,
name|e
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|elms
operator|=
name|e
expr_stmt|;
block|}
name|elms
index|[
name|count
operator|++
index|]
operator|=
name|node
expr_stmt|;
block|}
DECL|method|replace
specifier|public
specifier|synchronized
name|Node
name|replace
parameter_list|(
name|int
name|index
parameter_list|,
name|Node
name|replaceNode
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|count
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|count
condition|)
block|{
name|add
argument_list|(
name|replaceNode
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
name|Node
name|node
init|=
name|elms
index|[
name|index
index|]
decl_stmt|;
name|elms
index|[
name|index
index|]
operator|=
name|replaceNode
expr_stmt|;
return|return
name|node
return|;
block|}
block|}
comment|// XXX: TEST THIS METHOD!!!
DECL|method|insert
specifier|public
specifier|synchronized
name|Node
name|insert
parameter_list|(
name|int
name|index
parameter_list|,
name|Node
name|newNode
parameter_list|)
block|{
name|Node
name|res
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|index
argument_list|<
literal|0
operator|||
name|index
argument_list|>
name|count
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|count
condition|)
block|{
name|add
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|len
init|=
name|elms
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|len
operator|==
name|count
condition|)
block|{
name|Node
index|[]
name|e
init|=
operator|new
name|Node
index|[
name|len
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
literal|0
argument_list|,
name|e
argument_list|,
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
name|index
argument_list|,
name|e
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|count
operator|-
name|index
argument_list|)
expr_stmt|;
name|elms
operator|=
name|e
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
name|index
argument_list|,
name|elms
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|count
operator|-
name|index
argument_list|)
expr_stmt|;
block|}
name|res
operator|=
name|elms
index|[
name|index
index|]
expr_stmt|;
name|elms
index|[
name|index
index|]
operator|=
name|newNode
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|getIterator
specifier|public
name|NodeIterator
name|getIterator
parameter_list|()
block|{
return|return
operator|new
name|NodeIteratorImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
specifier|synchronized
name|Node
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|count
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
name|Node
name|node
init|=
name|elms
index|[
name|index
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|elms
argument_list|,
name|index
argument_list|,
name|count
operator|-
name|index
operator|-
literal|1
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|item
specifier|public
specifier|synchronized
name|Node
name|item
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|count
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|elms
index|[
name|index
index|]
return|;
block|}
comment|/** Returns the number of keys in this dictionary. */
DECL|method|getLength
specifier|public
specifier|synchronized
name|int
name|getLength
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|getPreviousNode
specifier|public
name|Node
name|getPreviousNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|elms
index|[
name|i
index|]
operator|==
name|node
condition|)
return|return
name|elms
index|[
name|i
operator|-
literal|1
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getNextNode
specifier|public
name|Node
name|getNextNode
parameter_list|(
name|Node
name|node
parameter_list|)
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
name|count
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|elms
index|[
name|i
index|]
operator|==
name|node
condition|)
return|return
name|elms
index|[
name|i
operator|+
literal|1
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|insertBefore
specifier|public
name|Node
name|insertBefore
parameter_list|(
name|Node
name|node
parameter_list|,
name|Node
name|ref
parameter_list|)
block|{
name|int
name|idx
init|=
name|index
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
operator|-
literal|1
condition|)
name|insert
argument_list|(
name|idx
argument_list|,
name|node
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|insertAfter
specifier|public
name|Node
name|insertAfter
parameter_list|(
name|Node
name|node
parameter_list|,
name|Node
name|ref
parameter_list|)
block|{
name|int
name|idx
init|=
name|index
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
operator|-
literal|1
condition|)
name|insert
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|node
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|replace
specifier|public
name|Node
name|replace
parameter_list|(
name|Node
name|node
parameter_list|,
name|Node
name|ref
parameter_list|)
block|{
return|return
name|replace
argument_list|(
name|index
argument_list|(
name|ref
argument_list|)
argument_list|,
name|node
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
name|Node
name|remove
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|int
name|idx
init|=
name|index
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
operator|(
name|idx
operator|>
operator|-
literal|1
condition|?
name|remove
argument_list|(
name|idx
argument_list|)
else|:
literal|null
operator|)
return|;
block|}
DECL|method|index
specifier|public
name|int
name|index
parameter_list|(
name|Node
name|node
parameter_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|elms
index|[
name|i
index|]
operator|==
name|node
condition|)
return|return
name|i
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|toString
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|boolean
name|f
init|=
literal|true
decl_stmt|;
name|int
name|count
init|=
name|getLength
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{ "
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|f
condition|)
block|{
name|f
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// FIXME: doesn't work properly when list changed underneath iterator
end_comment

begin_comment
comment|// proper thing would be to use observer pattern on current element--if
end_comment

begin_comment
comment|// this is removed we get callback and reposition the cursor... THISISAHACK!
end_comment

begin_comment
comment|// FIXME synchronize on the list itself.
end_comment

begin_class
DECL|class|NodeIteratorImpl
class|class
name|NodeIteratorImpl
implements|implements
name|NodeIterator
block|{
DECL|field|nlist
name|NodeListImpl
name|nlist
decl_stmt|;
DECL|field|index
name|int
name|index
decl_stmt|;
comment|/**      * Create iterator over the specified NodeList. The initial position      * will be one *before* the first element. Calling toNext() will      * position the iterator at the first element.      */
DECL|method|NodeIteratorImpl
specifier|public
name|NodeIteratorImpl
parameter_list|(
name|NodeListImpl
name|list
parameter_list|)
block|{
name|nlist
operator|=
name|list
expr_stmt|;
name|index
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|nlist
operator|.
name|getLength
argument_list|()
return|;
block|}
DECL|method|getCurrent
specifier|public
name|Node
name|getCurrent
parameter_list|()
block|{
return|return
operator|(
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|nlist
operator|.
name|count
operator|)
condition|?
name|nlist
operator|.
name|item
argument_list|(
name|index
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|toNext
specifier|public
name|Node
name|toNext
parameter_list|()
block|{
if|if
condition|(
name|index
operator|<
name|nlist
operator|.
name|count
condition|)
name|index
operator|++
expr_stmt|;
return|return
name|getCurrent
argument_list|()
return|;
block|}
DECL|method|toPrevious
specifier|public
name|Node
name|toPrevious
parameter_list|()
block|{
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
name|index
operator|--
expr_stmt|;
return|return
name|getCurrent
argument_list|()
return|;
block|}
DECL|method|toFirst
specifier|public
name|Node
name|toFirst
parameter_list|()
block|{
name|index
operator|=
literal|0
expr_stmt|;
return|return
name|getCurrent
argument_list|()
return|;
block|}
DECL|method|toLast
specifier|public
name|Node
name|toLast
parameter_list|()
block|{
name|index
operator|=
name|nlist
operator|.
name|count
expr_stmt|;
return|return
name|getCurrent
argument_list|()
return|;
block|}
DECL|method|toNth
specifier|public
name|Node
name|toNth
parameter_list|(
name|int
name|Nth
parameter_list|)
block|{
name|index
operator|=
name|Nth
expr_stmt|;
return|return
name|getCurrent
argument_list|()
return|;
block|}
comment|// FIXME: multi-threading problems here... (race condition)
DECL|method|toNode
specifier|public
name|Node
name|toNode
parameter_list|(
name|Node
name|destNode
parameter_list|)
block|{
name|int
name|idx
init|=
name|nlist
operator|.
name|index
argument_list|(
name|destNode
argument_list|)
decl_stmt|;
return|return
operator|(
name|idx
operator|>=
literal|0
condition|?
name|toNth
argument_list|(
name|idx
argument_list|)
else|:
literal|null
operator|)
return|;
block|}
block|}
end_class

end_unit

