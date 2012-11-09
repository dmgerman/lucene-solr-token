begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParsePosition
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BinaryDocValuesConsumer
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
name|codecs
operator|.
name|NumericDocValuesConsumer
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
name|codecs
operator|.
name|PerDocProducer
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
name|codecs
operator|.
name|SimpleDVConsumer
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
name|codecs
operator|.
name|SimpleDocValuesFormat
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
name|codecs
operator|.
name|SortedDocValuesConsumer
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
name|DocValues
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|SegmentInfo
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
name|SegmentReadState
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
name|SegmentWriteState
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
name|IOContext
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|BytesRef
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
name|IOUtils
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * plain text doc values format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextSimpleDocValuesFormat
specifier|public
class|class
name|SimpleTextSimpleDocValuesFormat
extends|extends
name|SimpleDocValuesFormat
block|{
DECL|field|END
specifier|final
specifier|static
name|BytesRef
name|END
init|=
operator|new
name|BytesRef
argument_list|(
literal|"END"
argument_list|)
decl_stmt|;
DECL|field|FIELD
specifier|final
specifier|static
name|BytesRef
name|FIELD
init|=
operator|new
name|BytesRef
argument_list|(
literal|"field "
argument_list|)
decl_stmt|;
comment|// used for numerics
DECL|field|MINVALUE
specifier|final
specifier|static
name|BytesRef
name|MINVALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  minvalue "
argument_list|)
decl_stmt|;
DECL|field|PATTERN
specifier|final
specifier|static
name|BytesRef
name|PATTERN
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  pattern "
argument_list|)
decl_stmt|;
comment|// used for bytes
DECL|field|MAXLENGTH
specifier|final
specifier|static
name|BytesRef
name|MAXLENGTH
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  maxlength "
argument_list|)
decl_stmt|;
DECL|field|LENGTH
specifier|final
specifier|static
name|BytesRef
name|LENGTH
init|=
operator|new
name|BytesRef
argument_list|(
literal|"length "
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|SimpleDVConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextDocValuesWriter
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|PerDocProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextDocValuesReader
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|maxDoc
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|)
return|;
block|}
comment|/** the .dat file contains the data.    *  for numbers this is a "fixed-width" file, for example a single byte range:    *<pre>    *  field myField    *    minvalue 0    *    pattern 000    *  005    *  234    *  123    *  ...    *</pre>    *  so a document's value (delta encoded from minvalue) can be retrieved by     *  seeking to startOffset + (1+pattern.length())*docid. The extra 1 is the newline.    *      *  for bytes this is also a "fixed-width" file, for example:    *<pre>    *  field myField    *    maxlength 8    *    pattern 0    *  length 6    *  foobar[space][space]    *  length 3    *  baz[space][space][space][space][space]    *  ...    *</pre>    *  so a document's value can be retrieved by seeking to startOffset + (9+pattern.length+maxlength)*docid    *  the extra 9 is 2 newlines, plus "length " itself.    *       *  the reader can just scan this file when it opens, skipping over the data blocks    *  and saving the offset/etc for each field.     */
DECL|class|SimpleTextDocValuesWriter
specifier|static
class|class
name|SimpleTextDocValuesWriter
extends|extends
name|SimpleDVConsumer
block|{
DECL|field|data
specifier|final
name|IndexOutput
name|data
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|SimpleTextDocValuesWriter
name|SimpleTextDocValuesWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
literal|"dat"
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|NumericDocValuesConsumer
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldEntry
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// write our minimum value to the .dat, all entries are deltas from that
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|MINVALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|minValue
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// build up our fixed-width "simple text packed ints" format
name|int
name|maxBytesPerValue
init|=
name|Long
operator|.
name|toString
argument_list|(
name|maxValue
operator|-
name|minValue
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|maxBytesPerValue
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
comment|// write our pattern to the .dat
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|PATTERN
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
specifier|final
name|DecimalFormat
name|encoder
init|=
operator|new
name|DecimalFormat
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValuesConsumer
argument_list|()
block|{
name|int
name|numDocsWritten
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|delta
init|=
name|value
operator|-
name|minValue
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|encoder
operator|.
name|format
argument_list|(
name|delta
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|numDocsWritten
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numDocs
operator|==
name|numDocsWritten
assert|;
comment|// nocommit: hopefully indexwriter is responsible for "filling" like it does stored fields!
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|BinaryDocValuesConsumer
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|boolean
name|fixedLength
parameter_list|,
specifier|final
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldEntry
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// write maxLength
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|MAXLENGTH
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|maxLength
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|int
name|maxBytesLength
init|=
name|Long
operator|.
name|toString
argument_list|(
name|maxLength
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|maxBytesLength
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
comment|// write our pattern for encoding lengths
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|PATTERN
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
specifier|final
name|DecimalFormat
name|encoder
init|=
operator|new
name|DecimalFormat
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryDocValuesConsumer
argument_list|()
block|{
name|int
name|numDocsWritten
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write length
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|LENGTH
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|encoder
operator|.
name|format
argument_list|(
name|value
operator|.
name|length
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// write bytes
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// pad to fit
for|for
control|(
name|int
name|i
init|=
name|value
operator|.
name|length
init|;
name|i
operator|<
name|maxLength
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|' '
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|numDocsWritten
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numDocs
operator|==
name|numDocsWritten
assert|;
comment|// nocommit: hopefully indexwriter is responsible for "filling" like it does stored fields!
block|}
block|}
return|;
block|}
comment|// nocommit
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|SortedDocValuesConsumer
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|boolean
name|fixedLength
parameter_list|,
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
comment|// nocommit
block|}
comment|/** write the header for this field */
DECL|method|writeFieldEntry
specifier|private
name|void
name|writeFieldEntry
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|FIELD
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|field
operator|.
name|name
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// TODO: sheisty to do this here?
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|END
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
empty_stmt|;
DECL|class|SimpleTextDocValuesReader
specifier|static
class|class
name|SimpleTextDocValuesReader
extends|extends
name|PerDocProducer
block|{
DECL|class|OneField
specifier|static
class|class
name|OneField
block|{
DECL|field|fieldInfo
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|dataStartFilePointer
name|long
name|dataStartFilePointer
decl_stmt|;
DECL|field|decoder
name|DecimalFormat
name|decoder
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
DECL|field|minValue
name|int
name|minValue
decl_stmt|;
block|}
DECL|field|data
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|fields
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OneField
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OneField
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SimpleTextDocValuesReader
name|SimpleTextDocValuesReader
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
literal|"dat"
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|scratch
operator|.
name|equals
argument_list|(
name|END
argument_list|)
condition|)
block|{
break|break;
block|}
assert|assert
name|startsWith
argument_list|(
name|FIELD
argument_list|)
assert|;
name|String
name|fieldName
init|=
name|stripPrefix
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
assert|assert
name|fieldInfo
operator|!=
literal|null
assert|;
name|OneField
name|field
init|=
operator|new
name|OneField
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|DocValues
operator|.
name|Type
name|dvType
init|=
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
assert|assert
name|dvType
operator|!=
literal|null
assert|;
switch|switch
condition|(
name|dvType
condition|)
block|{
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|PATTERN
argument_list|)
assert|;
name|field
operator|.
name|decoder
operator|=
operator|new
name|DecimalFormat
argument_list|(
name|stripPrefix
argument_list|(
name|PATTERN
argument_list|)
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|MAXLENGTH
argument_list|)
assert|;
name|field
operator|.
name|maxLength
operator|=
name|field
operator|.
name|decoder
operator|.
name|parse
argument_list|(
name|stripPrefix
argument_list|(
name|MAXLENGTH
argument_list|)
argument_list|,
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|+
name|field
operator|.
name|maxLength
operator|*
name|maxDoc
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
comment|// nocommit TODO
break|break;
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|MINVALUE
argument_list|)
assert|;
name|field
operator|.
name|minValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|stripPrefix
argument_list|(
name|MINVALUE
argument_list|)
argument_list|)
expr_stmt|;
name|readLine
argument_list|()
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|PATTERN
argument_list|)
assert|;
name|field
operator|.
name|decoder
operator|=
operator|new
name|DecimalFormat
argument_list|(
name|stripPrefix
argument_list|(
name|PATTERN
argument_list|)
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|+
name|field
operator|.
name|maxLength
operator|*
name|maxDoc
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
name|field
operator|.
name|dataStartFilePointer
operator|=
name|data
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// nocommit TODO
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readLine
specifier|private
name|void
name|readLine
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|data
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|startsWith
specifier|private
name|boolean
name|startsWith
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|prefix
argument_list|)
return|;
block|}
DECL|method|stripPrefix
specifier|private
name|String
name|stripPrefix
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
operator|+
name|prefix
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
operator|-
name|prefix
operator|.
name|length
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

