begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|com.relevanz.indyo.contenthandler
package|package
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|contenthandler
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

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
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|IndexDataSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|FSDataSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|relevanz
operator|.
name|indyo
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Enumeration
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
name|ZipException
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
name|ZipFile
import|;
end_import

begin_comment
comment|/**  * Handles Zip files.  *  * @author<a href="mailto:kelvint@apache.org">Kelvin Tan</a>  * @version $Id$   */
end_comment

begin_class
DECL|class|ZIPHandler
specifier|public
class|class
name|ZIPHandler
extends|extends
name|NestedFileContentHandlerAdapter
block|{
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
name|ZIPHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ZIPHandler
specifier|public
name|ZIPHandler
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
DECL|method|fileContentIsReadable
specifier|public
name|boolean
name|fileContentIsReadable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getNestedDataSource
specifier|public
name|List
name|getNestedDataSource
parameter_list|()
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|nestedDataSource
operator|==
literal|null
condition|)
block|{
name|nestedDataSource
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|ZipFile
name|zFile
init|=
operator|new
name|ZipFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
for|for
control|(
name|Enumeration
name|e
init|=
name|zFile
operator|.
name|entries
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|ZipEntry
name|entry
init|=
operator|(
name|ZipEntry
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|entryName
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|writeToTempFile
argument_list|(
name|zFile
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
argument_list|,
name|TEMP_FOLDER
operator|+
name|entryName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// create a new DataMap for each zip entry
name|IndexDataSource
name|ds
init|=
operator|new
name|FSDataSource
argument_list|(
name|TEMP_FOLDER
operator|+
name|entryName
argument_list|)
decl_stmt|;
name|nestedDataSource
operator|.
name|add
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
block|}
name|zFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZipException
name|ze
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
literal|"ZipException parsing zip:"
operator|+
name|ze
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ze
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|cat
operator|.
name|error
argument_list|(
literal|"IOException parsing zip:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|nestedDataSource
return|;
block|}
block|}
end_class

end_unit

