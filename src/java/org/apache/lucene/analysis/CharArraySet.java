begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Version
import|;
end_import

begin_comment
comment|/**  * A simple class that stores Strings as char[]'s in a  * hash table.  Note that this is not a general purpose  * class.  For example, it cannot remove items from the  * set, nor does it resize its hash table to be smaller,  * etc.  It is designed to be quick to test if a char[]  * is in the set without the necessity of converting it  * to a String first.  *<p>You must specify the required {@link Version}  * compatibility when creating {@link CharArraySet}:  *<ul>  *<li> As of 3.1, supplementary characters are  *       properly lowercased.</li>  *</ul>  * Before 3.1 supplementary characters could not be  * lowercased correctly due to the lack of Unicode 4  * support in JDK 1.4. To use instances of  * {@link CharArraySet} with the behavior before Lucene  * 3.1 pass a {@link Version}< 3.1 to the constructors.  *<P>  *<em>Please note:</em> This class implements {@link java.util.Set Set} but  * does not behave like it should in all cases. The generic type is  * {@code Set<Object>}, because you can add any object to it,  * that has a string representation. The add methods will use  * {@link Object#toString} and store the result using a {@code char[]}  * buffer. The same behavior have the {@code contains()} methods.  * The {@link #iterator()} returns an {@code Iterator<String>}.  * For type safety also {@link #stringIterator()} is provided.  */
end_comment

begin_class
DECL|class|CharArraySet
specifier|public
class|class
name|CharArraySet
extends|extends
name|AbstractSet
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|EMPTY_SET
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|EMPTY_SET
init|=
operator|new
name|CharArraySet
argument_list|(
name|CharArrayMap
operator|.
expr|<
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|PLACEHOLDER
specifier|private
specifier|static
specifier|final
name|Object
name|PLACEHOLDER
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|map
specifier|private
specifier|final
name|CharArrayMap
argument_list|<
name|Object
argument_list|>
name|map
decl_stmt|;
comment|/**    * Create set with enough capacity to hold startSize terms    *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details.    * @param startSize    *          the initial capacity    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    */
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|int
name|startSize
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|CharArrayMap
argument_list|<
name|Object
argument_list|>
argument_list|(
name|matchVersion
argument_list|,
name|startSize
argument_list|,
name|ignoreCase
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a set from a Collection of objects.     *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details.    * @param c    *          a collection whose elements to be placed into the set    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    */
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a set with enough capacity to hold startSize terms    *     * @param startSize    *          the initial capacity    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    * @deprecated use {@link #CharArraySet(Version, int, boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|int
name|startSize
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|startSize
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a set from a Collection of objects.     *     * @param c    *          a collection whose elements to be placed into the set    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    * @deprecated use {@link #CharArraySet(Version, Collection, boolean)} instead             */
annotation|@
name|Deprecated
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|/** Create set from the specified map (internal only), used also by {@link CharArrayMap#keySet()} */
DECL|method|CharArraySet
name|CharArraySet
parameter_list|(
specifier|final
name|CharArrayMap
argument_list|<
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
comment|/** Clears all entries in this set. This method is supported for reusing, but not {@link Set#remove}. */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** true if the<code>len</code> chars of<code>text</code> starting at<code>off</code>    * are in the set */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|text
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/** true if the<code>CharSequence</code> is in the set */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|CharSequence
name|cs
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|cs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|o
argument_list|,
name|PLACEHOLDER
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/** Add this CharSequence into the set */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|text
argument_list|,
name|PLACEHOLDER
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/** Add this String into the set */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|text
argument_list|,
name|PLACEHOLDER
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/** Add this char[] directly to the set.    * If ignoreCase is true for this Set, the text array will be directly modified.    * The user should never modify this text array after calling this method.    */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|char
index|[]
name|text
parameter_list|)
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|text
argument_list|,
name|PLACEHOLDER
argument_list|)
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns an unmodifiable {@link CharArraySet}. This allows to provide    * unmodifiable views of internal sets for "read-only" use.    *     * @param set    *          a set for which the unmodifiable set is returned.    * @return an new unmodifiable {@link CharArraySet}.    * @throws NullPointerException    *           if the given set is<code>null</code>.    */
DECL|method|unmodifiableSet
specifier|public
specifier|static
name|CharArraySet
name|unmodifiableSet
parameter_list|(
name|CharArraySet
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Given set is null"
argument_list|)
throw|;
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
if|if
condition|(
name|set
operator|.
name|map
operator|instanceof
name|CharArrayMap
operator|.
name|UnmodifiableCharArrayMap
condition|)
return|return
name|set
return|;
return|return
operator|new
name|CharArraySet
argument_list|(
name|CharArrayMap
operator|.
name|unmodifiableMap
argument_list|(
name|set
operator|.
name|map
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of the given set as a {@link CharArraySet}. If the given set    * is a {@link CharArraySet} the ignoreCase property will be preserved.    *     * @param set    *          a set to copy    * @return a copy of the given set as a {@link CharArraySet}. If the given set    *         is a {@link CharArraySet} the ignoreCase and matchVersion property will be    *         preserved.    * @deprecated use {@link #copy(Version, Set)} instead.    */
annotation|@
name|Deprecated
DECL|method|copy
specifier|public
specifier|static
name|CharArraySet
name|copy
parameter_list|(
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
return|return
name|copy
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|set
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of the given set as a {@link CharArraySet}. If the given set    * is a {@link CharArraySet} the ignoreCase property will be preserved.    *<p>    *<b>Note:</b> If you intend to create a copy of another {@link CharArraySet} where    * the {@link Version} of the source set differs from its copy    * {@link #CharArraySet(Version, Collection, boolean)} should be used instead.    * The {@link #copy(Version, Set)} will preserve the {@link Version} of the    * source set it is an instance of {@link CharArraySet}.    *</p>    *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details. This argument will be ignored if the    *          given set is a {@link CharArraySet}.    * @param set    *          a set to copy    * @return a copy of the given set as a {@link CharArraySet}. If the given set    *         is a {@link CharArraySet} the ignoreCase property as well as the    *         matchVersion will be of the given set will be preserved.    */
DECL|method|copy
specifier|public
specifier|static
name|CharArraySet
name|copy
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
if|if
condition|(
name|set
operator|instanceof
name|CharArraySet
condition|)
block|{
specifier|final
name|CharArraySet
name|source
init|=
operator|(
name|CharArraySet
operator|)
name|set
decl_stmt|;
return|return
operator|new
name|CharArraySet
argument_list|(
name|CharArrayMap
operator|.
name|copy
argument_list|(
name|source
operator|.
name|map
operator|.
name|matchVersion
argument_list|,
name|source
operator|.
name|map
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|set
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** The Iterator<String> for this set.  Strings are constructed on the fly, so    * use<code>nextCharArray</code> for more efficient access.    * @deprecated Use the standard iterator, which returns {@code char[]} instances.    */
annotation|@
name|Deprecated
DECL|class|CharArraySetIterator
specifier|public
class|class
name|CharArraySetIterator
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
DECL|field|pos
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|next
name|char
index|[]
name|next
decl_stmt|;
DECL|method|CharArraySetIterator
specifier|private
name|CharArraySetIterator
parameter_list|()
block|{
name|goNext
argument_list|()
expr_stmt|;
block|}
DECL|method|goNext
specifier|private
name|void
name|goNext
parameter_list|()
block|{
name|next
operator|=
literal|null
expr_stmt|;
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<
name|map
operator|.
name|keys
operator|.
name|length
operator|&&
operator|(
name|next
operator|=
name|map
operator|.
name|keys
index|[
name|pos
index|]
operator|)
operator|==
literal|null
condition|)
name|pos
operator|++
expr_stmt|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
comment|/** do not modify the returned char[] */
DECL|method|nextCharArray
specifier|public
name|char
index|[]
name|nextCharArray
parameter_list|()
block|{
name|char
index|[]
name|ret
init|=
name|next
decl_stmt|;
name|goNext
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/** Returns the next String, as a Set<String> would...      * use nextCharArray() for better efficiency. */
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|nextCharArray
argument_list|()
argument_list|)
return|;
block|}
DECL|method|remove
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
comment|/** returns an iterator of new allocated Strings (an instance of {@link CharArraySetIterator}).    * @deprecated Use {@link #iterator}, which returns {@code char[]} instances.    */
annotation|@
name|Deprecated
DECL|method|stringIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|stringIterator
parameter_list|()
block|{
return|return
operator|new
name|CharArraySetIterator
argument_list|()
return|;
block|}
comment|/** Returns an {@link Iterator} depending on the version used:    *<ul>    *<li>if {@code matchVersion}&ge; 3.1, it returns {@code char[]} instances in this set.</li>    *<li>if {@code matchVersion} is 3.0 or older, it returns new    * allocated Strings, so this method violates the Set interface.    * It is kept this way for backwards compatibility, normally it should    * return {@code char[]} on {@code next()}</li>    *</ul>    */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
comment|// use the AbstractSet#keySet()'s iterator (to not produce endless recursion)
return|return
name|map
operator|.
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|?
name|map
operator|.
name|originalKeySet
argument_list|()
operator|.
name|iterator
argument_list|()
else|:
operator|(
name|Iterator
operator|)
name|stringIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|item
range|:
name|this
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|char
index|[]
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
index|[]
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

