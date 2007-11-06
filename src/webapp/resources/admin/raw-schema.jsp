<%--
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ page import="org.apache.solr.core.SolrCore,
                 org.apache.solr.schema.IndexSchema"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.io.Reader"%>
<%@ page contentType="text/plain;charset=UTF-8" language="java" %>
<%
  Object ocore = request.getAttribute("org.apache.solr.SolrCore");
  SolrCore core = ocore instanceof SolrCore? (SolrCore) ocore : SolrCore.getSolrCore();
  IndexSchema schema = core.getSchema();
  Reader input = new InputStreamReader(schema.getInputStream());
  char[] buf = new char[4096];
  while (true) {
    int len = input.read(buf);
    if (len<=0) break;
    out.write(buf,0,len);
  }
%>
