begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopAnalyzer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|search
operator|.
name|Searcher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|search
operator|.
name|Hits
import|;
end_import

begin_import
import|import
name|com
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|QueryParser
import|;
end_import

begin_class
DECL|class|SearchFiles
class|class
name|SearchFiles
block|{
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
block|{
try|try
block|{
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StopAnalyzer
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"Query: "
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
operator|-
literal|1
condition|)
break|break;
name|Query
name|query
init|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|line
argument_list|,
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total matching documents"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|HITS_PER_PAGE
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|start
init|=
literal|0
init|;
name|start
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|start
operator|+=
name|HITS_PER_PAGE
control|)
block|{
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|,
name|start
operator|+
name|HITS_PER_PAGE
argument_list|)
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
name|end
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|". "
operator|+
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
name|end
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"more (y/n) ? "
argument_list|)
expr_stmt|;
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'n'
condition|)
break|break;
block|}
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

