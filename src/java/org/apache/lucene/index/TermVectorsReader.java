begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexInput
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|TermVectorsReader
class|class
name|TermVectorsReader
implements|implements
name|Cloneable
block|{
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|tvx
specifier|private
name|IndexInput
name|tvx
decl_stmt|;
DECL|field|tvd
specifier|private
name|IndexInput
name|tvd
decl_stmt|;
DECL|field|tvf
specifier|private
name|IndexInput
name|tvf
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|tvdFormat
specifier|private
name|int
name|tvdFormat
decl_stmt|;
DECL|field|tvfFormat
specifier|private
name|int
name|tvfFormat
decl_stmt|;
DECL|method|TermVectorsReader
name|TermVectorsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|d
operator|.
name|fileExists
argument_list|(
name|segment
operator|+
name|TermVectorsWriter
operator|.
name|TVX_EXTENSION
argument_list|)
condition|)
block|{
name|tvx
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
name|TermVectorsWriter
operator|.
name|TVX_EXTENSION
argument_list|)
expr_stmt|;
name|checkValidFormat
argument_list|(
name|tvx
argument_list|)
expr_stmt|;
name|tvd
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
name|TermVectorsWriter
operator|.
name|TVD_EXTENSION
argument_list|)
expr_stmt|;
name|tvdFormat
operator|=
name|checkValidFormat
argument_list|(
name|tvd
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
name|TermVectorsWriter
operator|.
name|TVF_EXTENSION
argument_list|)
expr_stmt|;
name|tvfFormat
operator|=
name|checkValidFormat
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
name|size
operator|=
operator|(
name|int
operator|)
name|tvx
operator|.
name|length
argument_list|()
operator|/
literal|8
expr_stmt|;
block|}
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
block|}
DECL|method|checkValidFormat
specifier|private
name|int
name|checkValidFormat
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|format
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|>
name|TermVectorsWriter
operator|.
name|FORMAT_VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible format version: "
operator|+
name|format
operator|+
literal|" expected "
operator|+
name|TermVectorsWriter
operator|.
name|FORMAT_VERSION
operator|+
literal|" or less"
argument_list|)
throw|;
block|}
return|return
name|format
return|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// make all effort to close up. Keep the first exception
comment|// and throw it as a new one.
name|IOException
name|keep
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
try|try
block|{
name|tvx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvd
operator|!=
literal|null
condition|)
try|try
block|{
name|tvd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvf
operator|!=
literal|null
condition|)
try|try
block|{
name|tvf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|keep
operator|.
name|fillInStackTrace
argument_list|()
throw|;
block|}
comment|/**    *     * @return The number of documents in the reader    */
DECL|method|size
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Retrieve the term vector for the given document and field    * @param docNum The document number to retrieve the vector for    * @param field The field within the document to retrieve    * @return The TermFreqVector for the document and field or null if there is no termVector for this field.    * @throws IOException if there is an error reading the term vector files    */
DECL|method|get
name|TermFreqVector
name|get
parameter_list|(
name|int
name|docNum
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if no term vectors are available for this segment at all
name|int
name|fieldNumber
init|=
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TermFreqVector
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|//We need to account for the FORMAT_SIZE at when seeking in the tvx
comment|//We don't need to do this in other seeks because we already have the
comment|// file pointer
comment|//that was written in another file
name|tvx
operator|.
name|seek
argument_list|(
operator|(
name|docNum
operator|*
literal|8L
operator|)
operator|+
name|TermVectorsWriter
operator|.
name|FORMAT_SIZE
argument_list|)
expr_stmt|;
comment|//System.out.println("TVX Pointer: " + tvx.getFilePointer());
name|long
name|position
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|int
name|fieldCount
init|=
name|tvd
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("Num Fields: " + fieldCount);
comment|// There are only a few fields per document. We opt for a full scan
comment|// rather then requiring that they be ordered. We need to read through
comment|// all of the fields anyway to get to the tvf pointers.
name|int
name|number
init|=
literal|0
decl_stmt|;
name|int
name|found
init|=
operator|-
literal|1
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tvdFormat
operator|==
name|TermVectorsWriter
operator|.
name|FORMAT_VERSION
condition|)
name|number
operator|=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
else|else
name|number
operator|+=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|number
operator|==
name|fieldNumber
condition|)
name|found
operator|=
name|i
expr_stmt|;
block|}
comment|// This field, although valid in the segment, was not found in this
comment|// document
if|if
condition|(
name|found
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Compute position in the tvf file
name|position
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
operator|<=
name|found
condition|;
name|i
operator|++
control|)
name|position
operator|+=
name|tvd
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|result
operator|=
name|readTermVector
argument_list|(
name|field
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("Fieldable not found");
block|}
block|}
else|else
block|{
comment|//System.out.println("No tvx file");
block|}
return|return
name|result
return|;
block|}
comment|/**    * Return all term vectors stored for this document or null if the could not be read in.    *     * @param docNum The document number to retrieve the vector for    * @return All term frequency vectors    * @throws IOException if there is an error reading the term vector files     */
DECL|method|get
name|TermFreqVector
index|[]
name|get
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
name|TermFreqVector
index|[]
name|result
init|=
literal|null
decl_stmt|;
comment|// Check if no term vectors are available for this segment at all
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|//We need to offset by
name|tvx
operator|.
name|seek
argument_list|(
operator|(
name|docNum
operator|*
literal|8L
operator|)
operator|+
name|TermVectorsWriter
operator|.
name|FORMAT_SIZE
argument_list|)
expr_stmt|;
name|long
name|position
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|int
name|fieldCount
init|=
name|tvd
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// No fields are vectorized for this document
if|if
condition|(
name|fieldCount
operator|!=
literal|0
condition|)
block|{
name|int
name|number
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
name|fieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tvdFormat
operator|==
name|TermVectorsWriter
operator|.
name|FORMAT_VERSION
condition|)
name|number
operator|=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
else|else
name|number
operator|+=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fields
index|[
name|i
index|]
operator|=
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|number
argument_list|)
expr_stmt|;
block|}
comment|// Compute position in the tvf file
name|position
operator|=
literal|0
expr_stmt|;
name|long
index|[]
name|tvfPointers
init|=
operator|new
name|long
index|[
name|fieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|position
operator|+=
name|tvd
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|tvfPointers
index|[
name|i
index|]
operator|=
name|position
expr_stmt|;
block|}
name|result
operator|=
name|readTermVectors
argument_list|(
name|fields
argument_list|,
name|tvfPointers
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//System.out.println("No tvx file");
block|}
return|return
name|result
return|;
block|}
DECL|method|readTermVectors
specifier|private
name|SegmentTermVector
index|[]
name|readTermVectors
parameter_list|(
name|String
name|fields
index|[]
parameter_list|,
name|long
name|tvfPointers
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentTermVector
name|res
index|[]
init|=
operator|new
name|SegmentTermVector
index|[
name|fields
operator|.
name|length
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
name|fields
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
name|readTermVector
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|tvfPointers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    *     * @param field The field to read in    * @param tvfPointer The pointer within the tvf file where we should start reading    * @return The TermVector located at that position    * @throws IOException    */
DECL|method|readTermVector
specifier|private
name|SegmentTermVector
name|readTermVector
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|tvfPointer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Now read the data from specified position
comment|//We don't need to offset by the FORMAT here since the pointer already includes the offset
name|tvf
operator|.
name|seek
argument_list|(
name|tvfPointer
argument_list|)
expr_stmt|;
name|int
name|numTerms
init|=
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("Num Terms: " + numTerms);
comment|// If no terms - return a constant empty termvector. However, this should never occur!
if|if
condition|(
name|numTerms
operator|==
literal|0
condition|)
return|return
operator|new
name|SegmentTermVector
argument_list|(
name|field
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
name|boolean
name|storePositions
decl_stmt|;
name|boolean
name|storeOffsets
decl_stmt|;
if|if
condition|(
name|tvfFormat
operator|==
name|TermVectorsWriter
operator|.
name|FORMAT_VERSION
condition|)
block|{
name|byte
name|bits
init|=
name|tvf
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|storePositions
operator|=
operator|(
name|bits
operator|&
name|TermVectorsWriter
operator|.
name|STORE_POSITIONS_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
expr_stmt|;
name|storeOffsets
operator|=
operator|(
name|bits
operator|&
name|TermVectorsWriter
operator|.
name|STORE_OFFSET_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|storePositions
operator|=
literal|false
expr_stmt|;
name|storeOffsets
operator|=
literal|false
expr_stmt|;
block|}
name|String
name|terms
index|[]
init|=
operator|new
name|String
index|[
name|numTerms
index|]
decl_stmt|;
name|int
name|termFreqs
index|[]
init|=
operator|new
name|int
index|[
name|numTerms
index|]
decl_stmt|;
comment|//  we may not need these, but declare them
name|int
name|positions
index|[]
index|[]
init|=
literal|null
decl_stmt|;
name|TermVectorOffsetInfo
name|offsets
index|[]
index|[]
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|storePositions
condition|)
name|positions
operator|=
operator|new
name|int
index|[
name|numTerms
index|]
index|[]
expr_stmt|;
if|if
condition|(
name|storeOffsets
condition|)
name|offsets
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|numTerms
index|]
index|[]
expr_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|deltaLength
init|=
literal|0
decl_stmt|;
name|int
name|totalLength
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
comment|// init the buffer with a length of 10 character
name|char
index|[]
name|previousBuffer
init|=
block|{}
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|start
operator|=
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|deltaLength
operator|=
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|totalLength
operator|=
name|start
operator|+
name|deltaLength
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
operator|<
name|totalLength
condition|)
block|{
comment|// increase buffer
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// give a hint to garbage collector
name|buffer
operator|=
operator|new
name|char
index|[
name|totalLength
index|]
expr_stmt|;
if|if
condition|(
name|start
operator|>
literal|0
condition|)
comment|// just copy if necessary
name|System
operator|.
name|arraycopy
argument_list|(
name|previousBuffer
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
name|tvf
operator|.
name|readChars
argument_list|(
name|buffer
argument_list|,
name|start
argument_list|,
name|deltaLength
argument_list|)
expr_stmt|;
name|terms
index|[
name|i
index|]
operator|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
name|previousBuffer
operator|=
name|buffer
expr_stmt|;
name|int
name|freq
init|=
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|termFreqs
index|[
name|i
index|]
operator|=
name|freq
expr_stmt|;
if|if
condition|(
name|storePositions
condition|)
block|{
comment|//read in the positions
name|int
index|[]
name|pos
init|=
operator|new
name|int
index|[
name|freq
index|]
decl_stmt|;
name|positions
index|[
name|i
index|]
operator|=
name|pos
expr_stmt|;
name|int
name|prevPosition
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|pos
index|[
name|j
index|]
operator|=
name|prevPosition
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|prevPosition
operator|=
name|pos
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storeOffsets
condition|)
block|{
name|TermVectorOffsetInfo
index|[]
name|offs
init|=
operator|new
name|TermVectorOffsetInfo
index|[
name|freq
index|]
decl_stmt|;
name|offsets
index|[
name|i
index|]
operator|=
name|offs
expr_stmt|;
name|int
name|prevOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|startOffset
init|=
name|prevOffset
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|startOffset
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|offs
index|[
name|j
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|prevOffset
operator|=
name|endOffset
expr_stmt|;
block|}
block|}
block|}
name|SegmentTermVector
name|tv
decl_stmt|;
if|if
condition|(
name|storePositions
operator|||
name|storeOffsets
condition|)
block|{
name|tv
operator|=
operator|new
name|SegmentTermPositionVector
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|termFreqs
argument_list|,
name|positions
argument_list|,
name|offsets
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tv
operator|=
operator|new
name|SegmentTermVector
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|termFreqs
argument_list|)
expr_stmt|;
block|}
return|return
name|tv
return|;
block|}
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
block|{
if|if
condition|(
name|tvx
operator|==
literal|null
operator|||
name|tvd
operator|==
literal|null
operator|||
name|tvf
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermVectorsReader
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|TermVectorsReader
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
name|clone
operator|.
name|tvx
operator|=
operator|(
name|IndexInput
operator|)
name|tvx
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|tvd
operator|=
operator|(
name|IndexInput
operator|)
name|tvd
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|tvf
operator|=
operator|(
name|IndexInput
operator|)
name|tvf
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

