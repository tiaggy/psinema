import { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, ScrollView } from 'react-native';
import { useAuthStore } from '../../store/authStore';
import { register } from '../../api/auth';

export default function RegisterScreen({ navigation }: any) {
  const [form, setForm] = useState({ email: '', password: '', firstName: '', lastName: '' });
  const [loading, setLoading] = useState(false);
  const { login: storeLogin } = useAuthStore();

  const handleRegister = async () => {
    if (!form.email || !form.password || !form.firstName || !form.lastName) {
      Alert.alert('Please fill in all fields');
      return;
    }
    setLoading(true);
    try {
      const data = await register(form);
      storeLogin(data.token, { email: data.email, role: data.role, firstName: data.firstName, lastName: data.lastName });
    } catch (e: any) {
      Alert.alert('Registration failed', e.response?.data?.message ?? 'Please try again');
    } finally {
      setLoading(false);
    }
  };

  const set = (k: string) => (v: string) => setForm(p => ({ ...p, [k]: v }));

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Create Account</Text>
      <TextInput style={styles.input} placeholder="First Name" value={form.firstName} onChangeText={set('firstName')} />
      <TextInput style={styles.input} placeholder="Last Name" value={form.lastName} onChangeText={set('lastName')} />
      <TextInput style={styles.input} placeholder="Email" value={form.email} onChangeText={set('email')} keyboardType="email-address" autoCapitalize="none" />
      <TextInput style={styles.input} placeholder="Password (min 6 chars)" value={form.password} onChangeText={set('password')} secureTextEntry />
      <TouchableOpacity style={styles.btn} onPress={handleRegister} disabled={loading}>
        <Text style={styles.btnText}>{loading ? 'Creating account…' : 'Register'}</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text style={styles.link}>Already have an account? Sign in</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flexGrow: 1, justifyContent: 'center', padding: 24, backgroundColor: '#1a1a2e' },
  title: { fontSize: 28, fontWeight: '700', color: '#e94560', textAlign: 'center', marginBottom: 28 },
  input: { backgroundColor: '#fff', borderRadius: 8, padding: 14, fontSize: 15, marginBottom: 14 },
  btn: { backgroundColor: '#e94560', borderRadius: 8, padding: 15, alignItems: 'center', marginBottom: 16 },
  btnText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  link: { color: '#aaa', textAlign: 'center', fontSize: 13 },
});
