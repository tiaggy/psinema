import { create } from 'zustand';

interface CartState {
  screeningId: number | null;
  selectedSeatIds: number[];
  reservationIds: number[];
  snackItems: { snackItemId: number; name: string; price: number; quantity: number }[];
  setScreening: (id: number) => void;
  toggleSeat: (id: number) => void;
  setReservations: (ids: number[]) => void;
  addSnack: (snackItemId: number, name: string, price: number) => void;
  removeSnack: (snackItemId: number) => void;
  clearCart: () => void;
}

export const useCartStore = create<CartState>((set) => ({
  screeningId: null,
  selectedSeatIds: [],
  reservationIds: [],
  snackItems: [],

  setScreening: (id) => set({ screeningId: id, selectedSeatIds: [], reservationIds: [] }),

  toggleSeat: (id) => set((s) => ({
    selectedSeatIds: s.selectedSeatIds.includes(id)
      ? s.selectedSeatIds.filter(i => i !== id)
      : [...s.selectedSeatIds, id],
  })),

  setReservations: (ids) => set({ reservationIds: ids }),

  addSnack: (snackItemId, name, price) => set((s) => {
    const existing = s.snackItems.find(i => i.snackItemId === snackItemId);
    if (existing) {
      return { snackItems: s.snackItems.map(i => i.snackItemId === snackItemId ? { ...i, quantity: i.quantity + 1 } : i) };
    }
    return { snackItems: [...s.snackItems, { snackItemId, name, price, quantity: 1 }] };
  }),

  removeSnack: (snackItemId) => set((s) => ({
    snackItems: s.snackItems.filter(i => i.snackItemId !== snackItemId),
  })),

  clearCart: () => set({ screeningId: null, selectedSeatIds: [], reservationIds: [], snackItems: [] }),
}));
