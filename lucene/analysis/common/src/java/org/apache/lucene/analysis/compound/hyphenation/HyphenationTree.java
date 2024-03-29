begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package

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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_comment
comment|/**  * This tree structure stores the hyphenation patterns in an efficient way for  * fast lookup. It provides the provides the method to hyphenate a word.  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.   */
end_comment

begin_class
DECL|class|HyphenationTree
specifier|public
class|class
name|HyphenationTree
extends|extends
name|TernaryTree
implements|implements
name|PatternConsumer
block|{
comment|/**    * value space: stores the interletter values    */
DECL|field|vspace
specifier|protected
name|ByteVector
name|vspace
decl_stmt|;
comment|/**    * This map stores hyphenation exceptions    */
DECL|field|stoplist
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|stoplist
decl_stmt|;
comment|/**    * This map stores the character classes    */
DECL|field|classmap
specifier|protected
name|TernaryTree
name|classmap
decl_stmt|;
comment|/**    * Temporary map to store interletter values on pattern loading.    */
DECL|field|ivalues
specifier|private
specifier|transient
name|TernaryTree
name|ivalues
decl_stmt|;
DECL|method|HyphenationTree
specifier|public
name|HyphenationTree
parameter_list|()
block|{
name|stoplist
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|23
argument_list|)
expr_stmt|;
comment|// usually a small table
name|classmap
operator|=
operator|new
name|TernaryTree
argument_list|()
expr_stmt|;
name|vspace
operator|=
operator|new
name|ByteVector
argument_list|()
expr_stmt|;
name|vspace
operator|.
name|alloc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// this reserves index 0, which we don't use
block|}
comment|/**    * Packs the values by storing them in 4 bits, two values into a byte Values    * range is from 0 to 9. We use zero as terminator, so we'll add 1 to the    * value.    *     * @param values a string of digits from '0' to '9' representing the    *        interletter values.    * @return the index into the vspace array where the packed values are stored.    */
DECL|method|packValues
specifier|protected
name|int
name|packValues
parameter_list|(
name|String
name|values
parameter_list|)
block|{
name|int
name|i
decl_stmt|,
name|n
init|=
name|values
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|m
init|=
operator|(
name|n
operator|&
literal|1
operator|)
operator|==
literal|1
condition|?
operator|(
name|n
operator|>>
literal|1
operator|)
operator|+
literal|2
else|:
operator|(
name|n
operator|>>
literal|1
operator|)
operator|+
literal|1
decl_stmt|;
name|int
name|offset
init|=
name|vspace
operator|.
name|alloc
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|byte
index|[]
name|va
init|=
name|vspace
operator|.
name|getArray
argument_list|()
decl_stmt|;
for|for
control|(
name|i
operator|=
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
name|int
name|j
init|=
name|i
operator|>>
literal|1
decl_stmt|;
name|byte
name|v
init|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|values
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
literal|'0'
operator|+
literal|1
operator|)
operator|&
literal|0x0f
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|1
operator|)
operator|==
literal|1
condition|)
block|{
name|va
index|[
name|j
operator|+
name|offset
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|va
index|[
name|j
operator|+
name|offset
index|]
operator||
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|va
index|[
name|j
operator|+
name|offset
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|<<
literal|4
argument_list|)
expr_stmt|;
comment|// big endian
block|}
block|}
name|va
index|[
name|m
operator|-
literal|1
operator|+
name|offset
index|]
operator|=
literal|0
expr_stmt|;
comment|// terminator
return|return
name|offset
return|;
block|}
DECL|method|unpackValues
specifier|protected
name|String
name|unpackValues
parameter_list|(
name|int
name|k
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|byte
name|v
init|=
name|vspace
operator|.
name|get
argument_list|(
name|k
operator|++
argument_list|)
decl_stmt|;
while|while
condition|(
name|v
operator|!=
literal|0
condition|)
block|{
name|char
name|c
init|=
call|(
name|char
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|4
operator|)
operator|-
literal|1
operator|+
literal|'0'
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|v
operator|&
literal|0x0f
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|1
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|v
operator|=
name|vspace
operator|.
name|get
argument_list|(
name|k
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Read hyphenation patterns from an XML file.    *     * @param source the InputSource for the file    * @throws IOException In case the parsing fails    */
DECL|method|loadPatterns
specifier|public
name|void
name|loadPatterns
parameter_list|(
name|InputSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|PatternParser
name|pp
init|=
operator|new
name|PatternParser
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|ivalues
operator|=
operator|new
name|TernaryTree
argument_list|()
expr_stmt|;
name|pp
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
comment|// patterns/values should be now in the tree
comment|// let's optimize a bit
name|trimToSize
argument_list|()
expr_stmt|;
name|vspace
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
name|classmap
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
comment|// get rid of the auxiliary map
name|ivalues
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|findPattern
specifier|public
name|String
name|findPattern
parameter_list|(
name|String
name|pat
parameter_list|)
block|{
name|int
name|k
init|=
name|super
operator|.
name|find
argument_list|(
name|pat
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|>=
literal|0
condition|)
block|{
return|return
name|unpackValues
argument_list|(
name|k
argument_list|)
return|;
block|}
return|return
literal|""
return|;
block|}
comment|/**    * String compare, returns 0 if equal or t is a substring of s    */
DECL|method|hstrcmp
specifier|protected
name|int
name|hstrcmp
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|si
parameter_list|,
name|char
index|[]
name|t
parameter_list|,
name|int
name|ti
parameter_list|)
block|{
for|for
control|(
init|;
name|s
index|[
name|si
index|]
operator|==
name|t
index|[
name|ti
index|]
condition|;
name|si
operator|++
operator|,
name|ti
operator|++
control|)
block|{
if|if
condition|(
name|s
index|[
name|si
index|]
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
if|if
condition|(
name|t
index|[
name|ti
index|]
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|s
index|[
name|si
index|]
operator|-
name|t
index|[
name|ti
index|]
return|;
block|}
DECL|method|getValues
specifier|protected
name|byte
index|[]
name|getValues
parameter_list|(
name|int
name|k
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|byte
name|v
init|=
name|vspace
operator|.
name|get
argument_list|(
name|k
operator|++
argument_list|)
decl_stmt|;
while|while
condition|(
name|v
operator|!=
literal|0
condition|)
block|{
name|char
name|c
init|=
call|(
name|char
call|)
argument_list|(
operator|(
name|v
operator|>>>
literal|4
operator|)
operator|-
literal|1
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|v
operator|&
literal|0x0f
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|v
operator|=
name|vspace
operator|.
name|get
argument_list|(
name|k
operator|++
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|res
init|=
operator|new
name|byte
index|[
name|buf
operator|.
name|length
argument_list|()
index|]
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|buf
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    *<p>    * Search for all possible partial matches of word starting at index an update    * interletter values. In other words, it does something like:    *</p>    *<code>    * for(i=0; i&lt;patterns.length; i++) {    * if ( word.substring(index).startsWidth(patterns[i]) )    * update_interletter_values(patterns[i]);    * }    *</code>    *<p>    * But it is done in an efficient way since the patterns are stored in a    * ternary tree. In fact, this is the whole purpose of having the tree: doing    * this search without having to test every single pattern. The number of    * patterns for languages such as English range from 4000 to 10000. Thus,    * doing thousands of string comparisons for each word to hyphenate would be    * really slow without the tree. The tradeoff is memory, but using a ternary    * tree instead of a trie, almost halves the the memory used by Lout or TeX.    * It's also faster than using a hash table    *</p>    *     * @param word null terminated word to match    * @param index start index from word    * @param il interletter values array to update    */
DECL|method|searchPatterns
specifier|protected
name|void
name|searchPatterns
parameter_list|(
name|char
index|[]
name|word
parameter_list|,
name|int
name|index
parameter_list|,
name|byte
index|[]
name|il
parameter_list|)
block|{
name|byte
index|[]
name|values
decl_stmt|;
name|int
name|i
init|=
name|index
decl_stmt|;
name|char
name|p
decl_stmt|,
name|q
decl_stmt|;
name|char
name|sp
init|=
name|word
index|[
name|i
index|]
decl_stmt|;
name|p
operator|=
name|root
expr_stmt|;
while|while
condition|(
name|p
operator|>
literal|0
operator|&&
name|p
operator|<
name|sc
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|sc
index|[
name|p
index|]
operator|==
literal|0xFFFF
condition|)
block|{
if|if
condition|(
name|hstrcmp
argument_list|(
name|word
argument_list|,
name|i
argument_list|,
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
name|values
operator|=
name|getValues
argument_list|(
name|eq
index|[
name|p
index|]
argument_list|)
expr_stmt|;
comment|// data pointer is in eq[]
name|int
name|j
init|=
name|index
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|values
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|j
argument_list|<
name|il
operator|.
name|length
operator|&&
name|values
index|[
name|k
index|]
argument_list|>
name|il
index|[
name|j
index|]
condition|)
block|{
name|il
index|[
name|j
index|]
operator|=
name|values
index|[
name|k
index|]
expr_stmt|;
block|}
name|j
operator|++
expr_stmt|;
block|}
block|}
return|return;
block|}
name|int
name|d
init|=
name|sp
operator|-
name|sc
index|[
name|p
index|]
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|sp
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|sp
operator|=
name|word
index|[
operator|++
name|i
index|]
expr_stmt|;
name|p
operator|=
name|eq
index|[
name|p
index|]
expr_stmt|;
name|q
operator|=
name|p
expr_stmt|;
comment|// look for a pattern ending at this position by searching for
comment|// the null char ( splitchar == 0 )
while|while
condition|(
name|q
operator|>
literal|0
operator|&&
name|q
operator|<
name|sc
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|sc
index|[
name|q
index|]
operator|==
literal|0xFFFF
condition|)
block|{
comment|// stop at compressed branch
break|break;
block|}
if|if
condition|(
name|sc
index|[
name|q
index|]
operator|==
literal|0
condition|)
block|{
name|values
operator|=
name|getValues
argument_list|(
name|eq
index|[
name|q
index|]
argument_list|)
expr_stmt|;
name|int
name|j
init|=
name|index
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|values
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|j
argument_list|<
name|il
operator|.
name|length
operator|&&
name|values
index|[
name|k
index|]
argument_list|>
name|il
index|[
name|j
index|]
condition|)
block|{
name|il
index|[
name|j
index|]
operator|=
name|values
index|[
name|k
index|]
expr_stmt|;
block|}
name|j
operator|++
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|q
operator|=
name|lo
index|[
name|q
index|]
expr_stmt|;
comment|/**              * actually the code should be: q = sc[q]< 0 ? hi[q] : lo[q]; but              * java chars are unsigned              */
block|}
block|}
block|}
else|else
block|{
name|p
operator|=
name|d
operator|<
literal|0
condition|?
name|lo
index|[
name|p
index|]
else|:
name|hi
index|[
name|p
index|]
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Hyphenate word and return a Hyphenation object.    *     * @param word the word to be hyphenated    * @param remainCharCount Minimum number of characters allowed before the    *        hyphenation point.    * @param pushCharCount Minimum number of characters allowed after the    *        hyphenation point.    * @return a {@link Hyphenation Hyphenation} object representing the    *         hyphenated word or null if word is not hyphenated.    */
DECL|method|hyphenate
specifier|public
name|Hyphenation
name|hyphenate
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|remainCharCount
parameter_list|,
name|int
name|pushCharCount
parameter_list|)
block|{
name|char
index|[]
name|w
init|=
name|word
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
return|return
name|hyphenate
argument_list|(
name|w
argument_list|,
literal|0
argument_list|,
name|w
operator|.
name|length
argument_list|,
name|remainCharCount
argument_list|,
name|pushCharCount
argument_list|)
return|;
block|}
comment|/**    * w = "****nnllllllnnn*****", where n is a non-letter, l is a letter, all n    * may be absent, the first n is at offset, the first l is at offset +    * iIgnoreAtBeginning; word = ".llllll.'\0'***", where all l in w are copied    * into word. In the first part of the routine len = w.length, in the second    * part of the routine len = word.length. Three indices are used: index(w),    * the index in w, index(word), the index in word, letterindex(word), the    * index in the letter part of word. The following relations exist: index(w) =    * offset + i - 1 index(word) = i - iIgnoreAtBeginning letterindex(word) =    * index(word) - 1 (see first loop). It follows that: index(w) - index(word) =    * offset - 1 + iIgnoreAtBeginning index(w) = letterindex(word) + offset +    * iIgnoreAtBeginning    */
comment|/**    * Hyphenate word and return an array of hyphenation points.    *     * @param w char array that contains the word    * @param offset Offset to first character in word    * @param len Length of word    * @param remainCharCount Minimum number of characters allowed before the    *        hyphenation point.    * @param pushCharCount Minimum number of characters allowed after the    *        hyphenation point.    * @return a {@link Hyphenation Hyphenation} object representing the    *         hyphenated word or null if word is not hyphenated.    */
DECL|method|hyphenate
specifier|public
name|Hyphenation
name|hyphenate
parameter_list|(
name|char
index|[]
name|w
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|remainCharCount
parameter_list|,
name|int
name|pushCharCount
parameter_list|)
block|{
name|int
name|i
decl_stmt|;
name|char
index|[]
name|word
init|=
operator|new
name|char
index|[
name|len
operator|+
literal|3
index|]
decl_stmt|;
comment|// normalize word
name|char
index|[]
name|c
init|=
operator|new
name|char
index|[
literal|2
index|]
decl_stmt|;
name|int
name|iIgnoreAtBeginning
init|=
literal|0
decl_stmt|;
name|int
name|iLength
init|=
name|len
decl_stmt|;
name|boolean
name|bEndOfLetters
init|=
literal|false
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|len
condition|;
name|i
operator|++
control|)
block|{
name|c
index|[
literal|0
index|]
operator|=
name|w
index|[
name|offset
operator|+
name|i
operator|-
literal|1
index|]
expr_stmt|;
name|int
name|nc
init|=
name|classmap
operator|.
name|find
argument_list|(
name|c
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|nc
operator|<
literal|0
condition|)
block|{
comment|// found a non-letter character ...
if|if
condition|(
name|i
operator|==
operator|(
literal|1
operator|+
name|iIgnoreAtBeginning
operator|)
condition|)
block|{
comment|// ... before any letter character
name|iIgnoreAtBeginning
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// ... after a letter character
name|bEndOfLetters
operator|=
literal|true
expr_stmt|;
block|}
name|iLength
operator|--
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|bEndOfLetters
condition|)
block|{
name|word
index|[
name|i
operator|-
name|iIgnoreAtBeginning
index|]
operator|=
operator|(
name|char
operator|)
name|nc
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
name|len
operator|=
name|iLength
expr_stmt|;
if|if
condition|(
name|len
operator|<
operator|(
name|remainCharCount
operator|+
name|pushCharCount
operator|)
condition|)
block|{
comment|// word is too short to be hyphenated
return|return
literal|null
return|;
block|}
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|len
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
comment|// check exception list first
name|String
name|sw
init|=
operator|new
name|String
argument_list|(
name|word
argument_list|,
literal|1
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|stoplist
operator|.
name|containsKey
argument_list|(
name|sw
argument_list|)
condition|)
block|{
comment|// assume only simple hyphens (Hyphen.pre="-", Hyphen.post = Hyphen.no =
comment|// null)
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|hw
init|=
name|stoplist
operator|.
name|get
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|hw
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|hw
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// j = index(sw) = letterindex(word)?
comment|// result[k] = corresponding index(w)
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|j
operator|+=
operator|(
operator|(
name|String
operator|)
name|o
operator|)
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|j
operator|>=
name|remainCharCount
operator|&&
name|j
operator|<
operator|(
name|len
operator|-
name|pushCharCount
operator|)
condition|)
block|{
name|result
index|[
name|k
operator|++
index|]
operator|=
name|j
operator|+
name|iIgnoreAtBeginning
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// use algorithm to get hyphenation points
name|word
index|[
literal|0
index|]
operator|=
literal|'.'
expr_stmt|;
comment|// word start marker
name|word
index|[
name|len
operator|+
literal|1
index|]
operator|=
literal|'.'
expr_stmt|;
comment|// word end marker
name|word
index|[
name|len
operator|+
literal|2
index|]
operator|=
literal|0
expr_stmt|;
comment|// null terminated
name|byte
index|[]
name|il
init|=
operator|new
name|byte
index|[
name|len
operator|+
literal|3
index|]
decl_stmt|;
comment|// initialized to zero
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|len
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|searchPatterns
argument_list|(
name|word
argument_list|,
name|i
argument_list|,
name|il
argument_list|)
expr_stmt|;
block|}
comment|// hyphenation points are located where interletter value is odd
comment|// i is letterindex(word),
comment|// i + 1 is index(word),
comment|// result[k] = corresponding index(w)
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|il
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|1
operator|)
operator|==
literal|1
operator|)
operator|&&
name|i
operator|>=
name|remainCharCount
operator|&&
name|i
operator|<=
operator|(
name|len
operator|-
name|pushCharCount
operator|)
condition|)
block|{
name|result
index|[
name|k
operator|++
index|]
operator|=
name|i
operator|+
name|iIgnoreAtBeginning
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|k
operator|>
literal|0
condition|)
block|{
comment|// trim result array
name|int
index|[]
name|res
init|=
operator|new
name|int
index|[
name|k
operator|+
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|res
argument_list|,
literal|1
argument_list|,
name|k
argument_list|)
expr_stmt|;
comment|// We add the synthetical hyphenation points
comment|// at the beginning and end of the word
name|res
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|res
index|[
name|k
operator|+
literal|1
index|]
operator|=
name|len
expr_stmt|;
return|return
operator|new
name|Hyphenation
argument_list|(
name|res
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Add a character class to the tree. It is used by    * {@link PatternParser PatternParser} as callback to add character classes.    * Character classes define the valid word characters for hyphenation. If a    * word contains a character not defined in any of the classes, it is not    * hyphenated. It also defines a way to normalize the characters in order to    * compare them with the stored patterns. Usually pattern files use only lower    * case characters, in this case a class for letter 'a', for example, should    * be defined as "aA", the first character being the normalization char.    */
annotation|@
name|Override
DECL|method|addClass
specifier|public
name|void
name|addClass
parameter_list|(
name|String
name|chargroup
parameter_list|)
block|{
if|if
condition|(
name|chargroup
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|char
name|equivChar
init|=
name|chargroup
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
index|[]
name|key
init|=
operator|new
name|char
index|[
literal|2
index|]
decl_stmt|;
name|key
index|[
literal|1
index|]
operator|=
literal|0
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
name|chargroup
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|key
index|[
literal|0
index|]
operator|=
name|chargroup
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|classmap
operator|.
name|insert
argument_list|(
name|key
argument_list|,
literal|0
argument_list|,
name|equivChar
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add an exception to the tree. It is used by    * {@link PatternParser PatternParser} class as callback to store the    * hyphenation exceptions.    *     * @param word normalized word    * @param hyphenatedword a vector of alternating strings and    *        {@link Hyphen hyphen} objects.    */
annotation|@
name|Override
DECL|method|addException
specifier|public
name|void
name|addException
parameter_list|(
name|String
name|word
parameter_list|,
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|hyphenatedword
parameter_list|)
block|{
name|stoplist
operator|.
name|put
argument_list|(
name|word
argument_list|,
name|hyphenatedword
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a pattern to the tree. Mainly, to be used by    * {@link PatternParser PatternParser} class as callback to add a pattern to    * the tree.    *     * @param pattern the hyphenation pattern    * @param ivalue interletter weight values indicating the desirability and    *        priority of hyphenating at a given point within the pattern. It    *        should contain only digit characters. (i.e. '0' to '9').    */
annotation|@
name|Override
DECL|method|addPattern
specifier|public
name|void
name|addPattern
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|ivalue
parameter_list|)
block|{
name|int
name|k
init|=
name|ivalues
operator|.
name|find
argument_list|(
name|ivalue
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<=
literal|0
condition|)
block|{
name|k
operator|=
name|packValues
argument_list|(
name|ivalue
argument_list|)
expr_stmt|;
name|ivalues
operator|.
name|insert
argument_list|(
name|ivalue
argument_list|,
operator|(
name|char
operator|)
name|k
argument_list|)
expr_stmt|;
block|}
name|insert
argument_list|(
name|pattern
argument_list|,
operator|(
name|char
operator|)
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|printStats
specifier|public
name|void
name|printStats
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Value space size = "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|vspace
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|printStats
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

