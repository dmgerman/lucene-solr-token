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
comment|/**  * An ordered Dictionary. keys() and elements() returns Enumerations  * which enumerate over elements in the order they were inserted.  * Elements are stored linearly. Operations put(), get(), and remove()  * are linear in the number of elements in the Dictionary.  *   *<p>Allows direct access to elements (as an alternative to using  * Enumerators) for speed.  *   *<p>Can function as a<em>bag</em>, i.e. it can be created with a mode  * which allows the same key to map to multiple entries. In this case   * operations get() and remove() operate on the<em>first</em> pair in  * the map. Hence to get hold of all values associated with a key it is  * necessary to use the direct access to underlying arrays.  *   * @author  Anders Kristensen  */
end_comment

begin_class
DECL|class|AttrListImpl
specifier|public
class|class
name|AttrListImpl
implements|implements
name|AttributeList
block|{
DECL|field|elms
specifier|protected
name|Attribute
index|[]
name|elms
decl_stmt|;
comment|/**      * Number of elements. The elements are held at indices 0 to n in elms.      */
DECL|field|n
specifier|protected
name|int
name|n
init|=
literal|0
decl_stmt|;
DECL|method|AttrListImpl
specifier|public
name|AttrListImpl
parameter_list|()
block|{
name|this
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an AttrListImpl with the specififed initial capacity.      */
DECL|method|AttrListImpl
specifier|public
name|AttrListImpl
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
literal|"Initial size must be at least 1"
argument_list|)
throw|;
name|elms
operator|=
operator|new
name|Attribute
index|[
name|size
index|]
expr_stmt|;
block|}
comment|/**      * Returns the value to which the key is mapped in this dictionary.       */
DECL|method|getAttribute
specifier|public
specifier|synchronized
name|Attribute
name|getAttribute
parameter_list|(
name|String
name|attrName
parameter_list|)
block|{
name|int
name|i
init|=
name|getIndex
argument_list|(
name|attrName
argument_list|)
decl_stmt|;
return|return
operator|(
name|i
operator|<
literal|0
condition|?
literal|null
else|:
name|elms
index|[
name|i
index|]
operator|)
return|;
block|}
DECL|method|getIndex
specifier|protected
name|int
name|getIndex
parameter_list|(
name|String
name|name
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
name|n
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
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|// XXX: what if attrName != attr.getName()???
DECL|method|setAttribute
specifier|public
specifier|synchronized
name|Attribute
name|setAttribute
parameter_list|(
name|Attribute
name|attr
parameter_list|)
block|{
name|int
name|i
init|=
name|getIndex
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|Attribute
name|old
init|=
name|elms
index|[
name|i
index|]
decl_stmt|;
name|elms
index|[
name|i
index|]
operator|=
name|attr
expr_stmt|;
return|return
name|old
return|;
block|}
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
name|n
condition|)
block|{
comment|// double size of key,elms arrays
name|AttrImpl
index|[]
name|e
decl_stmt|;
name|e
operator|=
operator|new
name|AttrImpl
index|[
name|len
operator|*
literal|2
index|]
expr_stmt|;
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
name|n
index|]
operator|=
name|attr
expr_stmt|;
name|n
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|remove
specifier|public
specifier|synchronized
name|Attribute
name|remove
parameter_list|(
name|String
name|attrName
parameter_list|)
block|{
name|int
name|i
init|=
name|getIndex
argument_list|(
name|attrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
return|return
literal|null
return|;
name|Attribute
name|val
init|=
name|elms
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|elms
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|elms
argument_list|,
name|i
argument_list|,
name|n
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|n
operator|--
expr_stmt|;
return|return
name|val
return|;
block|}
DECL|method|item
specifier|public
specifier|synchronized
name|Attribute
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
name|n
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
name|n
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
name|n
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
name|n
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
name|Attribute
name|attr
init|=
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
operator|+
literal|'='
operator|+
name|attr
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
comment|/**/
comment|// for testing
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
name|AttrListImpl
name|alist
decl_stmt|;
name|Attribute
name|attr
decl_stmt|;
name|java
operator|.
name|io
operator|.
name|BufferedReader
name|r
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|StringTokenizer
name|tok
decl_stmt|;
name|String
name|op
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|alist
operator|=
operator|new
name|AttrListImpl
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|alist
operator|=
operator|new
name|AttrListImpl
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Enter operations... op's are one of\n"
operator|+
literal|"put<key><val>\n"
operator|+
literal|"get<key>\n"
operator|+
literal|"rem<key>\n"
operator|+
literal|"size\n"
operator|+
literal|"quit\n"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|java
operator|.
name|io
operator|.
name|BufferedReader
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"doyourworst> "
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|java
operator|.
name|util
operator|.
name|StringTokenizer
argument_list|(
name|r
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|op
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"put"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|attr
operator|=
operator|new
name|AttrImpl
argument_list|(
name|tok
operator|.
name|nextToken
argument_list|()
argument_list|,
name|tok
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Value: "
operator|+
name|alist
operator|.
name|setAttribute
argument_list|(
name|attr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"get"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|attr
operator|=
name|alist
operator|.
name|getAttribute
argument_list|(
name|tok
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Value: "
operator|+
operator|(
name|attr
operator|==
literal|null
condition|?
literal|"No such element"
else|:
name|attr
operator|.
name|toString
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"rem"
operator|.
name|equals
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|attr
operator|=
name|alist
operator|.
name|remove
argument_list|(
name|tok
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Value: "
operator|+
name|attr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|.
name|startsWith
argument_list|(
literal|"s"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Size: "
operator|+
name|alist
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|op
operator|.
name|startsWith
argument_list|(
literal|"q"
argument_list|)
condition|)
block|{
break|break;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unrecognized op: "
operator|+
name|op
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"AttributeList: "
operator|+
name|alist
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Size: "
operator|+
name|alist
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
comment|//*/
block|}
end_class

end_unit

