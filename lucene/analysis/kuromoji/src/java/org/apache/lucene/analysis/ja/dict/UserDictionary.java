begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|Reader
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|analysis
operator|.
name|ja
operator|.
name|util
operator|.
name|CSVUtil
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
name|IntsRef
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
import|;
end_import

begin_comment
comment|/**  * Class for building a User Dictionary.  * This class allows for custom segmentation of phrases.  */
end_comment

begin_class
DECL|class|UserDictionary
specifier|public
specifier|final
class|class
name|UserDictionary
implements|implements
name|Dictionary
block|{
comment|// phrase text -> phrase ID
DECL|field|fst
specifier|private
specifier|final
name|TokenInfoFST
name|fst
decl_stmt|;
comment|// holds wordid, length, length... indexed by phrase ID
DECL|field|segmentations
specifier|private
specifier|final
name|int
name|segmentations
index|[]
index|[]
decl_stmt|;
comment|// holds readings and POS, indexed by wordid
DECL|field|data
specifier|private
specifier|final
name|String
name|data
index|[]
decl_stmt|;
DECL|field|CUSTOM_DICTIONARY_WORD_ID_OFFSET
specifier|private
specifier|static
specifier|final
name|int
name|CUSTOM_DICTIONARY_WORD_ID_OFFSET
init|=
literal|100000000
decl_stmt|;
DECL|field|WORD_COST
specifier|public
specifier|static
specifier|final
name|int
name|WORD_COST
init|=
operator|-
literal|100000
decl_stmt|;
DECL|field|LEFT_ID
specifier|public
specifier|static
specifier|final
name|int
name|LEFT_ID
init|=
literal|5
decl_stmt|;
DECL|field|RIGHT_ID
specifier|public
specifier|static
specifier|final
name|int
name|RIGHT_ID
init|=
literal|5
decl_stmt|;
DECL|method|UserDictionary
specifier|public
name|UserDictionary
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|int
name|wordId
init|=
name|CUSTOM_DICTIONARY_WORD_ID_OFFSET
decl_stmt|;
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|featureEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// text, segmentation, readings, POS
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Remove comments
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"#.*$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Skip empty lines or comment lines
if|if
condition|(
name|line
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|values
init|=
name|CSVUtil
operator|.
name|parse
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|featureEntries
operator|.
name|add
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|// TODO: should we allow multiple segmentations per input 'phrase'?
comment|// the old treemap didn't support this either, and i'm not sure if its needed/useful?
name|Collections
operator|.
name|sort
argument_list|(
name|featureEntries
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
index|[]
name|left
parameter_list|,
name|String
index|[]
name|right
parameter_list|)
block|{
return|return
name|left
index|[
literal|0
index|]
operator|.
name|compareTo
argument_list|(
name|right
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|featureEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|segmentations
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|(
name|featureEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|PositiveIntOutputs
name|fstOutput
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Builder
argument_list|<
name|Long
argument_list|>
name|fstBuilder
init|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE2
argument_list|,
name|fstOutput
argument_list|)
decl_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|long
name|ord
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
index|[]
name|values
range|:
name|featureEntries
control|)
block|{
name|String
index|[]
name|segmentation
init|=
name|values
index|[
literal|1
index|]
operator|.
name|replaceAll
argument_list|(
literal|"  *"
argument_list|,
literal|" "
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
index|[]
name|readings
init|=
name|values
index|[
literal|2
index|]
operator|.
name|replaceAll
argument_list|(
literal|"  *"
argument_list|,
literal|" "
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
name|pos
init|=
name|values
index|[
literal|3
index|]
decl_stmt|;
if|if
condition|(
name|segmentation
operator|.
name|length
operator|!=
name|readings
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Illegal user dictionary entry "
operator|+
name|values
index|[
literal|0
index|]
operator|+
literal|" - the number of segmentations ("
operator|+
name|segmentation
operator|.
name|length
operator|+
literal|")"
operator|+
literal|" does not the match number of readings ("
operator|+
name|readings
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|int
index|[]
name|wordIdAndLength
init|=
operator|new
name|int
index|[
name|segmentation
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
comment|// wordId offset, length, length....
name|wordIdAndLength
index|[
literal|0
index|]
operator|=
name|wordId
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
name|segmentation
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|wordIdAndLength
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|segmentation
index|[
name|i
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|readings
index|[
name|i
index|]
operator|+
name|INTERNAL_SEPARATOR
operator|+
name|pos
argument_list|)
expr_stmt|;
name|wordId
operator|++
expr_stmt|;
block|}
comment|// add mapping to FST
name|String
name|token
init|=
name|values
index|[
literal|0
index|]
decl_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
name|token
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|token
operator|.
name|length
argument_list|()
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
name|token
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|scratch
operator|.
name|ints
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|token
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|fstBuilder
operator|.
name|add
argument_list|(
name|scratch
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|segmentations
operator|.
name|add
argument_list|(
name|wordIdAndLength
argument_list|)
expr_stmt|;
name|ord
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|fst
operator|=
operator|new
name|TokenInfoFST
argument_list|(
name|fstBuilder
operator|.
name|finish
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|data
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentations
operator|=
name|segmentations
operator|.
name|toArray
argument_list|(
operator|new
name|int
index|[
name|segmentations
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Lookup words in text    * @param chars text    * @param off offset into text    * @param len length of text    * @return array of {wordId, position, length}    */
DECL|method|lookup
specifier|public
name|int
index|[]
index|[]
name|lookup
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: can we avoid this treemap/toIndexArray?
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|result
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// index, [length, length...]
name|boolean
name|found
init|=
literal|false
decl_stmt|;
comment|// true if we found any results
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|off
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|startOffset
init|=
name|off
init|;
name|startOffset
operator|<
name|end
condition|;
name|startOffset
operator|++
control|)
block|{
name|arc
operator|=
name|fst
operator|.
name|getFirstArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
name|int
name|output
init|=
literal|0
decl_stmt|;
name|int
name|remaining
init|=
name|end
operator|-
name|startOffset
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
name|remaining
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ch
init|=
name|chars
index|[
name|startOffset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|ch
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|,
name|i
operator|==
literal|0
argument_list|,
name|fstReader
argument_list|)
operator|==
literal|null
condition|)
block|{
break|break;
comment|// continue to next position
block|}
name|output
operator|+=
name|arc
operator|.
name|output
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
specifier|final
name|int
name|finalOutput
init|=
name|output
operator|+
name|arc
operator|.
name|nextFinalOutput
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|startOffset
operator|-
name|off
argument_list|,
name|segmentations
index|[
name|finalOutput
index|]
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|found
condition|?
name|toIndexArray
argument_list|(
name|result
argument_list|)
else|:
name|EMPTY_RESULT
return|;
block|}
DECL|method|getFST
specifier|public
name|TokenInfoFST
name|getFST
parameter_list|()
block|{
return|return
name|fst
return|;
block|}
DECL|field|EMPTY_RESULT
specifier|private
specifier|static
specifier|final
name|int
index|[]
index|[]
name|EMPTY_RESULT
init|=
operator|new
name|int
index|[
literal|0
index|]
index|[]
decl_stmt|;
comment|/**    * Convert Map of index and wordIdAndLength to array of {wordId, index, length}    * @param input    * @return array of {wordId, index, length}    */
DECL|method|toIndexArray
specifier|private
name|int
index|[]
index|[]
name|toIndexArray
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|input
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|input
operator|.
name|keySet
argument_list|()
control|)
block|{
name|int
index|[]
name|wordIdAndLength
init|=
name|input
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|wordId
init|=
name|wordIdAndLength
index|[
literal|0
index|]
decl_stmt|;
comment|// convert length to index
name|int
name|current
init|=
name|i
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|wordIdAndLength
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// first entry is wordId offset
name|int
index|[]
name|token
init|=
block|{
name|wordId
operator|+
name|j
operator|-
literal|1
block|,
name|current
block|,
name|wordIdAndLength
index|[
name|j
index|]
block|}
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|current
operator|+=
name|wordIdAndLength
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|int
index|[
name|result
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
return|;
block|}
DECL|method|lookupSegmentation
specifier|public
name|int
index|[]
name|lookupSegmentation
parameter_list|(
name|int
name|phraseID
parameter_list|)
block|{
return|return
name|segmentations
index|[
name|phraseID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getLeftId
specifier|public
name|int
name|getLeftId
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|LEFT_ID
return|;
block|}
annotation|@
name|Override
DECL|method|getRightId
specifier|public
name|int
name|getRightId
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|RIGHT_ID
return|;
block|}
annotation|@
name|Override
DECL|method|getWordCost
specifier|public
name|int
name|getWordCost
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|WORD_COST
return|;
block|}
annotation|@
name|Override
DECL|method|getReading
specifier|public
name|String
name|getReading
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|getFeature
argument_list|(
name|wordId
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPartOfSpeech
specifier|public
name|String
name|getPartOfSpeech
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
name|getFeature
argument_list|(
name|wordId
argument_list|,
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBaseForm
specifier|public
name|String
name|getBaseForm
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO: add support?
block|}
annotation|@
name|Override
DECL|method|getPronunciation
specifier|public
name|String
name|getPronunciation
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
name|surface
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO: add support?
block|}
annotation|@
name|Override
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO: add support?
block|}
annotation|@
name|Override
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO: add support?
block|}
DECL|method|getAllFeaturesArray
specifier|private
name|String
index|[]
name|getAllFeaturesArray
parameter_list|(
name|int
name|wordId
parameter_list|)
block|{
name|String
name|allFeatures
init|=
name|data
index|[
name|wordId
operator|-
name|CUSTOM_DICTIONARY_WORD_ID_OFFSET
index|]
decl_stmt|;
if|if
condition|(
name|allFeatures
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|allFeatures
operator|.
name|split
argument_list|(
name|INTERNAL_SEPARATOR
argument_list|)
return|;
block|}
DECL|method|getFeature
specifier|private
name|String
name|getFeature
parameter_list|(
name|int
name|wordId
parameter_list|,
name|int
modifier|...
name|fields
parameter_list|)
block|{
name|String
index|[]
name|allFeatures
init|=
name|getAllFeaturesArray
argument_list|(
name|wordId
argument_list|)
decl_stmt|;
if|if
condition|(
name|allFeatures
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// All features
for|for
control|(
name|String
name|feature
range|:
name|allFeatures
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|CSVUtil
operator|.
name|quoteEscape
argument_list|(
name|feature
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// One feature doesn't need to escape value
name|sb
operator|.
name|append
argument_list|(
name|allFeatures
index|[
name|fields
index|[
literal|0
index|]
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|field
range|:
name|fields
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|CSVUtil
operator|.
name|quoteEscape
argument_list|(
name|allFeatures
index|[
name|field
index|]
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

