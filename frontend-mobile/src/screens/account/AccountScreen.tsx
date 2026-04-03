import { View, Text, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { useAuthStore } from '../../store/authStore';

export default function AccountScreen({ navigation }: any) {
  const { user, isAuthenticated, logout } = useAuthStore();

  if (!isAuthenticated()) {
    return (
      <View style={styles.container}>
        <Text style={styles.heading}>Account</Text>
        <Text style={styles.subtitle}>Sign in to access your account, orders, and tickets.</Text>
        <TouchableOpacity style={styles.btn} onPress={() => navigation.navigate('Login')}>
          <Text style={styles.btnText}>Sign In</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.btnOutline} onPress={() => navigation.navigate('Register')}>
          <Text style={styles.btnOutlineText}>Create Account</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const handleLogout = () => {
    Alert.alert('Sign out', 'Are you sure?', [
      { text: 'Cancel' },
      { text: 'Sign out', style: 'destructive', onPress: logout },
    ]);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Account</Text>
      <View style={styles.profileCard}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>{user?.firstName?.[0]}{user?.lastName?.[0]}</Text>
        </View>
        <Text style={styles.name}>{user?.firstName} {user?.lastName}</Text>
        <Text style={styles.email}>{user?.email}</Text>
        <Text style={styles.role}>{user?.role}</Text>
      </View>
      <TouchableOpacity style={styles.logoutBtn} onPress={handleLogout}>
        <Text style={styles.logoutText}>Sign Out</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 24 },
  heading: { fontSize: 22, fontWeight: '700', color: '#1a1a2e', marginBottom: 16 },
  subtitle: { fontSize: 14, color: '#888', marginBottom: 24, lineHeight: 20 },
  btn: { backgroundColor: '#e94560', borderRadius: 8, padding: 14, alignItems: 'center', marginBottom: 12 },
  btnText: { color: '#fff', fontWeight: '600', fontSize: 15 },
  btnOutline: { borderWidth: 1, borderColor: '#e94560', borderRadius: 8, padding: 14, alignItems: 'center' },
  btnOutlineText: { color: '#e94560', fontWeight: '600', fontSize: 15 },
  profileCard: { backgroundColor: '#fff', borderRadius: 12, padding: 24, alignItems: 'center', marginBottom: 24, elevation: 1 },
  avatar: { width: 72, height: 72, borderRadius: 36, backgroundColor: '#e94560', alignItems: 'center', justifyContent: 'center', marginBottom: 12 },
  avatarText: { color: '#fff', fontSize: 24, fontWeight: '700' },
  name: { fontSize: 18, fontWeight: '700', color: '#1a1a2e' },
  email: { fontSize: 13, color: '#888', marginTop: 4 },
  role: { fontSize: 11, color: '#e94560', marginTop: 4, fontWeight: '600' },
  logoutBtn: { borderWidth: 1, borderColor: '#e74c3c', borderRadius: 8, padding: 14, alignItems: 'center' },
  logoutText: { color: '#e74c3c', fontWeight: '600', fontSize: 15 },
});
