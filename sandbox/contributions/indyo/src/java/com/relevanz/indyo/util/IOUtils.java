begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|com.relevanz.indyo.util
package|package
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|util
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|com
operator|.
name|ice
operator|.
name|tar
operator|.
name|TarArchive
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_comment
comment|/**  * Utility IO-related methods.  *  * @author<a href="mailto:kelvint@apache.org">Kelvin Tan</a>  * @version $Id$   */
end_comment

begin_class
DECL|class|IOUtils
specifier|public
specifier|final
class|class
name|IOUtils
block|{
comment|/**      * Log4j category.      */
DECL|field|cat
specifier|private
specifier|static
name|Category
name|cat
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|IOUtils
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Writes data from the inputstream to the outputstream.      *      * @param in InputStream to read from.      * @param out OutputStream to write to.      * @throws IOException I/O error.      */
DECL|method|transferData
specifier|public
specifier|static
name|void
name|transferData
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|10000
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Recursively deletes a directory.      * @param File Directory to delete.      */
DECL|method|deleteDirectory
specifier|public
specifier|static
name|void
name|deleteDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|File
index|[]
name|fArray
init|=
name|directory
operator|.
name|listFiles
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
name|fArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fArray
index|[
name|i
index|]
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|deleteDirectory
argument_list|(
name|fArray
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|fArray
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/**      * Writes an input stream to a temporary file which is set      * to delete when the VM exits.      * @param Inputstream to read data from      * @param Temporary file to write to      */
DECL|method|writeToTempFile
specifier|public
specifier|static
name|void
name|writeToTempFile
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|tempfile
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|tempfile
argument_list|)
decl_stmt|;
name|f
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|char
name|lastChar
init|=
name|tempfile
operator|.
name|charAt
argument_list|(
name|tempfile
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// make no assumptions that java.io.File detects directories
comment|// in a cross-platform manner
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
operator|||
name|lastChar
operator|==
literal|'\\'
operator|||
name|lastChar
operator|==
literal|'/'
condition|)
name|f
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
else|else
block|{
comment|// ensure that all necessary directories are created
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|parent
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|parent
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|tempfile
argument_list|)
expr_stmt|;
name|transferData
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Writes an file to a ZipOutputStream.      * @param File to read data from      * @param Path of the ZipEntry      * @param ZipOutputStream to write to      */
DECL|method|addToZipOutputStream
specifier|public
specifier|static
name|void
name|addToZipOutputStream
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|zipPath
parameter_list|,
name|ZipOutputStream
name|out
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
comment|// Create a buffer for copying
name|int
name|bytes_read
decl_stmt|;
name|FileInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// Stream to read file
name|ZipEntry
name|entry
init|=
operator|new
name|ZipEntry
argument_list|(
name|zipPath
argument_list|)
decl_stmt|;
comment|// Make a ZipEntry
name|out
operator|.
name|putNextEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// Store entry in zipfile
while|while
condition|(
operator|(
name|bytes_read
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
comment|// Copy bytes to zipfile
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytes_read
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Close input stream
block|}
block|}
comment|/**      * Extracts a tar file to a directory.      * @param Tar file to read data from      * @param Directory to write to      */
DECL|method|extractTar
specifier|public
specifier|static
name|void
name|extractTar
parameter_list|(
name|File
name|tarFile
parameter_list|,
name|File
name|destDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|tarFile
argument_list|)
expr_stmt|;
name|TarArchive
name|ta
init|=
operator|new
name|TarArchive
argument_list|(
name|fis
argument_list|)
decl_stmt|;
name|ta
operator|.
name|extractContents
argument_list|(
name|destDir
argument_list|)
expr_stmt|;
name|ta
operator|.
name|closeArchive
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fis
operator|!=
literal|null
condition|)
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Extracts a GZip file to a file.      * @param GZip file to read data from      * @param File to write to      */
DECL|method|extractGZip
specifier|public
specifier|static
name|void
name|extractGZip
parameter_list|(
name|File
name|f
parameter_list|,
name|File
name|destFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|GZIPInputStream
name|gzin
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|destFile
argument_list|)
expr_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|gzin
operator|=
operator|new
name|GZIPInputStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|10000
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|gzin
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|gzin
operator|!=
literal|null
condition|)
name|gzin
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|fis
operator|!=
literal|null
condition|)
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * reads all bytes from the given stream      * @param is the stream to read from      */
DECL|method|loadBytes
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|loadBytes
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read in the entry data
name|int
name|count
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|byte
index|[]
name|t
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|length
operator|+
name|count
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|t
expr_stmt|;
block|}
return|return
name|buffer
return|;
block|}
comment|/** Returns the file extension of a file.      * @param filename Filename to obtain the file extension.      * @return File extension (without the ".").      */
DECL|method|getFileExtension
specifier|public
specifier|static
name|String
name|getFileExtension
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
return|return
name|filename
operator|.
name|substring
argument_list|(
name|filename
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|)
return|;
comment|// + 1 to remove the "."
block|}
comment|/** Returns the file extension of a file.      * @param f File object to obtain the file extension.      * @return File extension (without the ".").      */
DECL|method|getFileExtension
specifier|public
specifier|static
name|String
name|getFileExtension
parameter_list|(
name|File
name|f
parameter_list|)
block|{
return|return
name|getFileExtension
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

