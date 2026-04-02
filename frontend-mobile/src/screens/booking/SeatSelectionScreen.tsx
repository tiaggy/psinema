import { View, Text, TouchableOpacity, StyleSheet, ActivityIndicator, Alert, ScrollView } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { getScreeningSeats } from '../../api/screenings';
import { reserveSeats } from '../../api/reservations';
import { useCartStore } from '../../store/cartStore';
import { useAuthStore } from '../../store/authStore';
import HallMap from '../../components/HallMap';

export default function SeatSelectionScreen({ route, navigation }: any) {
  const { screening } = route.params;
  const { isAuthenticated } = useAuthStore();
  const { selectedSeatIds, toggleSeat, setScreening, setReservations } = useCartStore();
  const { data: seats, isLoading } = useQuery({
    queryKey: ['seats', screening.id],
    queryFn: () => getScreeningSeats(screening.id),
    onSuccess: () => setScreening(screening.id),
  });

  const handleContinue = async () => {
    if (!isAuthenticated()) {
      Alert.alert('Login required', 'Please log in to purchase tickets.', [
        { text: 'Cancel' },
        { text: 'Login', onPress: () => navigation.navigate('Account') },
      ]);
      return;
    }
    if (selectedSeatIds.length === 0) {
      Alert.alert('Select seats', 'Please select at least one seat.');
      return;
    }
    try {
      const res = await reserveSeats(screening.id, selectedSeatIds);
      setReservations(res.reservationIds);
      navigation.navigate('SnackOrder', { screening });
    } catch (e: any) {
      Alert.alert('Error', e.response?.data?.message ?? 'Could not reserve seats');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{screening.movie.title}</Text>
      <Text style={styles.subtitle}>{new Date(screening.startTime).toLocaleString()} · {screening.hall.name}</Text>
      {isLoading ? <ActivityIndicator color="#e94560" style={{ marginTop: 40 }} /> : (
        <ScrollView style={styles.mapArea}>
          <HallMap seats={seats ?? []} selectedSeatIds={selectedSeatIds} onToggleSeat={toggleSeat} />
        </ScrollView>
      )}
      <View style={styles.footer}>
        <Text style={styles.footerInfo}>{selectedSeatIds.length} seat(s) · €{(selectedSeatIds.length * (screening.basePrice ?? 0)).toFixed(2)}</Text>
        <TouchableOpacity style={styles.btn} onPress={handleContinue}>
          <Text style={styles.btnText}>Continue →</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  title: { fontSize: 18, fontWeight: '700', color: '#1a1a2e', padding: 16, paddingBottom: 4 },
  subtitle: { fontSize: 12, color: '#888', paddingHorizontal: 16, marginBottom: 12 },
  mapArea: { flex: 1, padding: 8 },
  footer: { backgroundColor: '#fff', padding: 16, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', elevation: 8 },
  footerInfo: { fontSize: 15, fontWeight: '600', color: '#1a1a2e' },
  btn: { backgroundColor: '#e94560', borderRadius: 8, paddingHorizontal: 20, paddingVertical: 12 },
  btnText: { color: '#fff', fontWeight: '600', fontSize: 15 },
});
