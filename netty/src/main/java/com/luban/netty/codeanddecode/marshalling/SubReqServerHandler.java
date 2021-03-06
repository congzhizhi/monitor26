/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.luban.netty.codeanddecode.marshalling;

import com.luban.netty.codeanddecode.googleprotobuf.SubscribeReqProto;
import com.luban.netty.codeanddecode.googleprotobuf.SubscribeRespProto;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
@Sharable
public class SubReqServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
	SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
	if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
	    System.out.println("Service accept client subscrib req : ["
		    + req.toString() + "]");
	    ctx.writeAndFlush(resp(req.getSubReqID()));
	}
    }

    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
		SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
		builder.setSubReqID(subReqID);
		builder.setRespCode("0");
		builder.setDesc("Netty book order successd!");
	return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	cause.printStackTrace();
	ctx.close();// 发生异常，关闭链路
    }
}
