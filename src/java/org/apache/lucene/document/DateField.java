begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/** Provides support for converting dates to strings and vice-versa.  The    * strings are structured so that lexicographic sorting orders by date.  This    * makes them suitable for use as field values and search terms.  */
end_comment

begin_class
DECL|class|DateField
specifier|public
class|class
name|DateField
block|{
DECL|method|DateField
specifier|private
name|DateField
parameter_list|()
block|{}
empty_stmt|;
comment|// make date strings long enough to last a millenium
DECL|field|DATE_LEN
specifier|private
specifier|static
name|int
name|DATE_LEN
init|=
name|Long
operator|.
name|toString
argument_list|(
literal|1000L
operator|*
literal|365
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|method|MIN_DATE_STRING
specifier|public
specifier|static
name|String
name|MIN_DATE_STRING
parameter_list|()
block|{
return|return
name|timeToString
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|MAX_DATE_STRING
specifier|public
specifier|static
name|String
name|MAX_DATE_STRING
parameter_list|()
block|{
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|DATE_LEN
index|]
decl_stmt|;
name|char
name|c
init|=
name|Character
operator|.
name|forDigit
argument_list|(
name|Character
operator|.
name|MAX_RADIX
operator|-
literal|1
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
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
name|DATE_LEN
condition|;
name|i
operator|++
control|)
name|buffer
index|[
name|i
index|]
operator|=
name|c
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/** Converts a Date to a string suitable for indexing. */
DECL|method|dateToString
specifier|public
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
return|return
name|timeToString
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a millisecond time to a string suitable for indexing. */
DECL|method|timeToString
specifier|public
specifier|static
name|String
name|timeToString
parameter_list|(
name|long
name|time
parameter_list|)
block|{
if|if
condition|(
name|time
operator|<
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"time too early"
argument_list|)
throw|;
name|String
name|s
init|=
name|Long
operator|.
name|toString
argument_list|(
name|time
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
name|DATE_LEN
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"time too late"
argument_list|)
throw|;
while|while
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|DATE_LEN
condition|)
name|s
operator|=
literal|"0"
operator|+
name|s
expr_stmt|;
comment|// pad with leading zeros
return|return
name|s
return|;
block|}
comment|/** Converts a string-encoded date into a millisecond time. */
DECL|method|stringToTime
specifier|public
specifier|static
name|long
name|stringToTime
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
return|;
block|}
comment|/** Converts a string-encoded date into a Date object. */
DECL|method|stringToDate
specifier|public
specifier|static
name|Date
name|stringToDate
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|stringToTime
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

