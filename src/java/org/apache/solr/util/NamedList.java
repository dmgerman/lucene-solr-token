begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * A simple container class for modeling an ordered list of name/value pairs.  *  *<p>  * Unlike Maps:  *</p>  *<ul>  *<li>Names may be repeated</li>  *<li>Order of elements is maintained</li>  *<li>Elements may be accessed by numeric index</li>  *<li>Names and Values can both be null</li>  *</ul>  *  *<p>  * :TODO: In the future, it would be nice if this extended Map or Collection,  * had iterators, used java5 generics, had a faster lookup for  * large lists, etc...  * It could also have an interface, and multiple implementations.  * One might have indexed lookup, one might not.  *</p>  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|NamedList
specifier|public
class|class
name|NamedList
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Cloneable
implements|,
name|Serializable
implements|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|>
block|{
DECL|field|nvPairs
specifier|protected
specifier|final
name|List
name|nvPairs
decl_stmt|;
comment|/** Creates an empty instance */
DECL|method|NamedList
specifier|public
name|NamedList
parameter_list|()
block|{
name|nvPairs
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates an instance backed by an explicitly specified list of    * pairwise names/values.    *    * @param nameValuePairs underlying List which should be used to implement a NamedList; modifying this List will affect the NamedList.    */
DECL|method|NamedList
specifier|public
name|NamedList
parameter_list|(
name|List
name|nameValuePairs
parameter_list|)
block|{
name|nvPairs
operator|=
name|nameValuePairs
expr_stmt|;
block|}
comment|/** The total number of name/value pairs */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|nvPairs
operator|.
name|size
argument_list|()
operator|>>
literal|1
return|;
block|}
comment|/**    * The name of the pair at the specified List index    *    * @return null if no name exists    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|nvPairs
operator|.
name|get
argument_list|(
name|idx
operator|<<
literal|1
argument_list|)
return|;
block|}
comment|/**    * The value of the pair at the specified List index    *    * @return may be null    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getVal
specifier|public
name|T
name|getVal
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|nvPairs
operator|.
name|get
argument_list|(
operator|(
name|idx
operator|<<
literal|1
operator|)
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**    * Adds a name/value pair to the end of the list.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|val
parameter_list|)
block|{
name|nvPairs
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nvPairs
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Modifies the name of the pair at the specified index.    */
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|int
name|idx
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|nvPairs
operator|.
name|set
argument_list|(
name|idx
operator|<<
literal|1
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Modifies the value of the pair at the specified index.    * @return the value that used to be at index    */
DECL|method|setVal
specifier|public
name|T
name|setVal
parameter_list|(
name|int
name|idx
parameter_list|,
name|T
name|val
parameter_list|)
block|{
name|int
name|index
init|=
operator|(
name|idx
operator|<<
literal|1
operator|)
operator|+
literal|1
decl_stmt|;
name|T
name|old
init|=
operator|(
name|T
operator|)
name|nvPairs
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|nvPairs
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
comment|/**    * Scans the list sequentially beginning at the specified index and    * returns the index of the first pair with the specified name.    *    * @param name name to look for, may be null    * @param start index to begin searching from    * @return The index of the first matching pair, -1 if no match    */
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|sz
init|=
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|==
literal|null
condition|)
return|return
name|i
return|;
comment|// matched null
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|n
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
comment|/**    * Gets the value for the first instance of the specified name    * found.    *     * @return null if not found or if the value stored was null.    * @see #indexOf    * @see #get(String,int)    */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Gets the value for the first instance of the specified name    * found starting at the specified index.    *     * @return null if not found or if the value stored was null.    * @see #indexOf    */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|sz
init|=
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|==
literal|null
condition|)
return|return
name|getVal
argument_list|(
name|i
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
return|return
name|getVal
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|toString
specifier|public
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
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|int
name|sz
init|=
name|size
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getName
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Iterates over the Map and sequentially adds it's key/value pairs    */
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|args
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|entry
range|:
name|args
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/** Appends the elements of the given NamedList to this one. */
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|NamedList
argument_list|<
name|T
argument_list|>
name|nl
parameter_list|)
block|{
name|nvPairs
operator|.
name|addAll
argument_list|(
name|nl
operator|.
name|nvPairs
argument_list|)
expr_stmt|;
return|return
name|nl
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Makes a<i>shallow copy</i> of the named list.    */
DECL|method|clone
specifier|public
name|NamedList
name|clone
parameter_list|()
block|{
name|ArrayList
name|newList
init|=
operator|new
name|ArrayList
argument_list|(
name|nvPairs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|newList
operator|.
name|addAll
argument_list|(
name|nvPairs
argument_list|)
expr_stmt|;
return|return
operator|new
name|NamedList
argument_list|(
name|newList
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------------------------
comment|// Iterable interface
comment|//----------------------------------------------------------------------------
comment|/**    * Support the Iterable interface    */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|NamedList
name|list
init|=
name|this
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|>
name|iter
init|=
operator|new
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|idx
operator|<
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|next
parameter_list|()
block|{
specifier|final
name|int
name|index
init|=
name|idx
operator|++
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|nv
init|=
operator|new
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|list
operator|.
name|getName
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|T
name|getValue
parameter_list|()
block|{
return|return
operator|(
name|T
operator|)
name|list
operator|.
name|getVal
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|getValue
argument_list|()
return|;
block|}
specifier|public
name|T
name|setValue
parameter_list|(
name|T
name|value
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|list
operator|.
name|setVal
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
name|nv
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
name|iter
return|;
block|}
block|}
end_class

end_unit

