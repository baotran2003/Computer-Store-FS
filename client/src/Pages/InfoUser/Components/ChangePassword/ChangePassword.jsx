import React, { useEffect, useState } from 'react';
import { Form, Input, Button, Card, message, Alert } from 'antd';
import { LockOutlined, KeyOutlined, CheckOutlined, GoogleOutlined } from '@ant-design/icons';
import styles from './ChangePassword.module.scss';
import classNames from 'classnames/bind';
import { requestChangePassword } from '../../../../config/request';
import { useStore } from '../../../../hooks/useStore';

const cx = classNames.bind(styles);

function ChangePassword() {
    const [form] = Form.useForm();
    const { dataUser } = useStore();
    const [isGoogleUser, setIsGoogleUser] = useState(false);

    useEffect(() => {
        // Check if user is Google login user
        if (dataUser?.typeLogin === 'GOOGLE' || dataUser?.typeLogin === 'google') {
            setIsGoogleUser(true);
        }
    }, [dataUser]);

    const onFinish = async (values) => {
        try {
            if (values.newPassword !== values.confirmPassword) {
                message.error('Mật khẩu xác nhận không khớp!');
                return;
            }

            const data = {
                currentPassword: values.currentPassword,
                newPassword: values.newPassword,
                confirmPassword: values.confirmPassword,
            };

            await requestChangePassword(data);
            message.success('Thay đổi mật khẩu thành công!');
            form.resetFields();
        } catch (error) {
            message.error(error.response?.data?.message || 'Có lỗi xảy ra khi thay đổi mật khẩu!');
        }
    };

    // If user is Google login, show message instead of form
    if (isGoogleUser) {
        return (
            <Card title="Thay đổi mật khẩu" className={cx('password-card')}>
                <Alert
                    message="Tài khoản Google"
                    description={
                        <div>
                            <p>Tài khoản của bạn được đăng nhập qua Google. Để thay đổi mật khẩu, vui lòng:</p>
                            <ol style={{ marginTop: '12px', paddingLeft: '20px' }}>
                                <li>Truy cập <a href="https://myaccount.google.com/security" target="_blank" rel="noopener noreferrer">Tài khoản Google</a></li>
                                <li>Chọn mục "Bảo mật"</li>
                                <li>Thay đổi mật khẩu trong phần "Đăng nhập vào Google"</li>
                            </ol>
                        </div>
                    }
                    type="info"
                    icon={<GoogleOutlined />}
                    showIcon
                />
            </Card>
        );
    }

    return (
        <Card title="Thay đổi mật khẩu" className={cx('password-card')}>
            <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                className={cx('form')}
                autoComplete="off"
            >
                <Form.Item
                    name="currentPassword"
                    label="Mật khẩu hiện tại"
                    className={cx('form-item')}
                    rules={[
                        { required: true, message: 'Vui lòng nhập mật khẩu hiện tại!' }
                    ]}
                >
                    <Input.Password 
                        prefix={<LockOutlined />} 
                        placeholder="Nhập mật khẩu hiện tại"
                        size="large"
                    />
                </Form.Item>

                <Form.Item
                    name="newPassword"
                    label="Mật khẩu mới"
                    className={cx('form-item')}
                    rules={[
                        { required: true, message: 'Vui lòng nhập mật khẩu mới!' },
                        { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự!' },
                        {
                            pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                            message: 'Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số!'
                        }
                    ]}
                >
                    <Input.Password 
                        prefix={<KeyOutlined />} 
                        placeholder="Nhập mật khẩu mới"
                        size="large"
                    />
                </Form.Item>

                <Form.Item
                    name="confirmPassword"
                    label="Xác nhận mật khẩu mới"
                    className={cx('form-item')}
                    dependencies={['newPassword']}
                    rules={[
                        { required: true, message: 'Vui lòng xác nhận mật khẩu mới!' },
                        ({ getFieldValue }) => ({
                            validator(_, value) {
                                if (!value || getFieldValue('newPassword') === value) {
                                    return Promise.resolve();
                                }
                                return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                            },
                        }),
                    ]}
                >
                    <Input.Password 
                        prefix={<CheckOutlined />} 
                        placeholder="Xác nhận mật khẩu mới"
                        size="large"
                    />
                </Form.Item>

                <Form.Item className={cx('submit-button')}>
                    <Button type="primary" htmlType="submit" size="large">
                        Thay đổi mật khẩu
                    </Button>
                </Form.Item>
            </Form>

            <div className={cx('password-requirements')}>
                <h4>Yêu cầu mật khẩu:</h4>
                <ul>
                    <li>Ít nhất 6 ký tự</li>
                    <li>Có ít nhất 1 chữ hoa (A-Z)</li>
                    <li>Có ít nhất 1 chữ thường (a-z)</li>
                    <li>Có ít nhất 1 chữ số (0-9)</li>
                </ul>
            </div>
        </Card>
    );
}

export default ChangePassword;