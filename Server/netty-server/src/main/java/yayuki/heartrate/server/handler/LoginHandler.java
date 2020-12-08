package yayuki.heartrate.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import yayuki.heartrate.net.protobuf.LoginProto;
import yayuki.heartrate.server.data.*;
import yayuki.heartrate.server.repository.ChannelRepository;
import yayuki.heartrate.server.repository.UserRepository;

import static yayuki.heartrate.server.data.ResultUtil.errResult;

@ChannelHandler.Sharable
public class LoginHandler extends SimpleChannelInboundHandler<LoginProto.Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginProto.Request request) throws Exception {
        //使用者登入

        ctx.executor().execute(() -> {
            LoginProto.Result result = login(request, ctx.channel());

            if (result.getRetCode() != -1) {
                System.out.println(ctx);
                System.out.println(request.getCredentials().getAccount());
                System.out.println(result.getUser());
            }
            ctx.writeAndFlush(result);
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logout(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught: " + cause);
    }

    private LoginProto.Result login(LoginProto.Request request, Channel channel) {
        LoginProto.Credentials credentials = request.getCredentials();
        String account = credentials.getAccount();
        String password = credentials.getPassword();
        switch (request.getStatus()) {
            case CARER:
                return UserRepository.INSTANCE.loginCarer(account, password).map(carer -> {
                    onCarerLogin(carer, channel);
                    return ResultUtil.createCarerResult(carer);
                }).orElse(errResult);
            case PATIENT:
                return UserRepository.INSTANCE.loginPatient(account, password).map(patient -> {
                    onPatientLogin(patient, channel);
                    return ResultUtil.createPatientResult(patient);
                }).orElse(errResult);
            default:
                return errResult;
        }
    }

    private void onUserLogin(User user, Channel channel) {
        System.out.println("login: " + user);
        channel.attr(User.ATTR_ID).set(user.getID());
    }

    private void onCarerLogin(Carer carer, Channel channel) {
        onUserLogin(carer, channel);
        channel.attr(User.ATTR_STATUS).set(UserStatus.Carer);
        ChannelRepository.INSTANCE.addCarer(carer.getID(), channel);
    }

    private void onPatientLogin(Patient patient, Channel channel) {
        onUserLogin(patient, channel);
        int carerID = patient.getCarerID();

        channel.attr(User.ATTR_STATUS).set(UserStatus.Patient);

        ChannelRepository.INSTANCE.addPatient(patient.getID(), channel);

        channel.pipeline().addLast(new StateHandler(carerID));
    }

    public void logout(Channel channel) {
        UserStatus status = channel.attr(User.ATTR_STATUS).get();
        Integer id = channel.attr(User.ATTR_ID).get();

        if (status == null || id == null) {
            System.out.println("Channel is Not Login");
            return;
        }

        System.out.println("logout: " + status + " - " + id);
        switch (status) {
            case Carer:
                ChannelRepository.INSTANCE.removeCarer(id);
                break;
            case Patient:
                ChannelRepository.INSTANCE.removePatient(id);
                break;
        }
    }
}
